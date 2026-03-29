@F<template>
  <div class="container-xl py-5 write-container">
    <form @submit.prevent="submitForm" class="mx-auto modern-write-card shadow-sm">

      <header class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="m-0 fw-bold text-dark">새 글 쓰기</h3>
        <button class="btn-vote-soft" type="button" @click="goToVote">
          <i class="fas fa-poll me-1"></i> 투표 작성
        </button>
      </header>

      <div class="d-flex align-items-center gap-3 mb-4 p-3 bg-light-soft rounded-4">
        <div class="form-check form-switch m-0 custom-switch">
          <input id="anonymous" class="form-check-input" type="checkbox" v-model="anonymous" />
          <label class="form-check-label fw-semibold text-secondary ms-1" for="anonymous">익명 사용</label>
        </div>
        <Transition name="fade-slide">
          <input
              v-if="anonymous"
              v-model="username"
              class="modern-input flex-grow-1"
              placeholder="사용할 익명 닉네임을 입력하세요"
          />
        </Transition>
      </div>

      <div class="mb-4">
        <select v-model="category" class="modern-select mb-3" required>
          <option disabled value="">카테고리 선택</option>
          <option v-for="opt in categories" :key="opt" :value="opt">{{ opt }}</option>
        </select>

        <input
            v-model="title"
            class="title-input"
            placeholder="제목을 입력하세요"
            required
        />
      </div>

      <div class="editor-wrapper mb-4">
        <div class="editor-toolbar">
          <button type="button" class="toolbar-btn" @click="applyFormat('bold')" title="굵게">
            <i class="bi bi-type-bold" />
          </button>
          <div class="toolbar-divider"></div>
          <button type="button" class="toolbar-btn" @click="applyFormat('link')" title="링크 삽입">
            <i class="bi bi-link-45deg" />
          </button>
          <button type="button" class="toolbar-btn" @click="applyFormat('code')" title="코드 블럭">
            <i class="bi bi-code-slash" />
          </button>
          <div class="toolbar-divider"></div>
          <button type="button" class="toolbar-btn" @click="$refs.imageInput.click()" :disabled="pendingFiles.length >= MAX_IMAGES" title="이미지 삽입">
            <i class="bi bi-image" />
          </button>
          <span v-if="pendingFiles.length > 0" class="img-count-badge">{{ pendingFiles.length }}/{{ MAX_IMAGES }}</span>
          <input
              ref="imageInput"
              type="file"
              accept="image/*"
              multiple
              class="d-none"
              @change="handleFiles"
          />
        </div>

        <div
            class="editor-content-area position-relative"
            @dragover.prevent="onEditorDragOver"
            @dragleave="onEditorDragLeave"
            @drop.prevent="onEditorDrop"
            :class="{ 'drag-over': isDragOver }"
        >
          <p v-if="showPlaceholder" class="editor-placeholder">이곳에 자유롭게 내용을 작성해보세요...<br><span class="placeholder-img-hint"><i class="bi bi-image me-1"></i>이미지를 여기에 드래그하거나 위 버튼을 눌러 삽입하세요</span></p>
          <div
              ref="editor"
              class="content-editor"
              contenteditable
              @input="onEditorInput"
              @focus="showPlaceholder = false"
              @blur="checkEditorEmpty"
          ></div>
        </div>
      </div>


      <button class="btn-submit-glow w-100" :disabled="isSubmitting">
        <span v-if="isSubmitting">
          <i class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></i> 업로드 중...
        </span>
        <span v-else>
          <i class="bi bi-pencil-square me-2"></i> 작성 완료하기
        </span>
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {useUserStore} from "@/stores/user.js";
import {onBeforeUnmount, nextTick } from 'vue'
const router           = useRouter()
const anonymous        = ref(false)
const username         = ref('')
const category         = ref('')
const title            = ref('')
const store   = useUserStore()
const imageInput = ref(null)
const MAX_IMAGES = 3
const pendingFiles = ref([])
function toast(msg){ alert(msg) }
const categories       = ['자유', '자격증', '문제', '기술', '취업', 'Q/A', '자료','고민']
const showPlaceholder  = ref(true)
const editor           = ref(null)
const isSubmitting     = ref(false)
const isDragOver       = ref(false)

const onEditorDragOver = () => { isDragOver.value = true }
const onEditorDragLeave = () => { isDragOver.value = false }
const onEditorDrop = (e) => {
  isDragOver.value = false
  const files = e.dataTransfer?.files
  if (files?.length) handleFiles(files)
}

const applyFormat = type => {
  if (type === 'link') {
    const url = prompt('URL 입력')
    if (url) document.execCommand('createLink', false, url)
  } else {
    document.execCommand(type, false, null)
  }
}
const checkEditorEmpty = () => (showPlaceholder.value = !editor.value?.innerText.trim())
const onEditorInput    = () => checkEditorEmpty()



async function handleFiles (e) {
  const list = e.target?.files ? Array.from(e.target.files) : Array.from(e)
  const remain = MAX_IMAGES - pendingFiles.value.length
  if (remain <= 0) { toast(`이미지는 최대 ${MAX_IMAGES}장까지 업로드할 수 있어요.`); resetChooser(); return }

  const chosen = list.slice(0, remain)
  if (list.length > remain) toast(`최대 ${MAX_IMAGES}장까지 가능: ${remain}장만 추가됩니다.`)

  for (const file of chosen) {
    if (!file.type.startsWith('image/')) continue
    const id  = crypto.randomUUID()
    const url = URL.createObjectURL(file)
    const img = new Image()
    await new Promise(r => { img.onload = r; img.src = url })

    const p = {
      id, file, url,
      width: 480,
      height: Math.round(480 * (img.naturalHeight / img.naturalWidth)),
      ratio: img.naturalWidth / img.naturalHeight
    }
    pendingFiles.value.push(p)
    await nextTick()
    insertFromTray(p)
  }
  resetChooser()
}

function insertAtCaret(node) {
  const sel = window.getSelection()
  editor.value?.appendChild(node)
  const newLine = document.createElement('br');
  editor.value?.appendChild(newLine);
  if (sel) {
    const range = document.createRange()
    range.setStartAfter(node)
    range.collapse(true)
    sel.removeAllRanges()
    sel.addRange(range)
  }
}
function resetChooser(){
  if (imageInput.value) {
    imageInput.value.value = '';
  }
}
function removePending(id){
  // 본문에서 제거
  const wrap = editor.value?.querySelector(`.image-wrapper[data-id="${id}"]`)
  if (wrap) wrap.remove()
  // 목록에서 제거
  const idx = pendingFiles.value.findIndex(p => p.id === id)
  if (idx >= 0) {
    URL.revokeObjectURL(pendingFiles.value[idx].url)
    pendingFiles.value.splice(idx, 1)
  }
}

function getEditorWidth () {
  const el = editor.value
  if (!el) return 720
  const rect = el.getBoundingClientRect()
  return Math.max(320, Math.min(720, Math.round(rect.width - 28)))
}
let _onResize
onMounted(async () => {
  _onResize = () => clampImagesToContainer()
  window.addEventListener('resize', _onResize)
  await nextTick()
  clampImagesToContainer()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', _onResize)
})
function clampImagesToContainer () {
  const containerW = getEditorWidth()
  editor.value?.querySelectorAll('.image-wrapper').forEach(wrap => {
    const img = wrap.querySelector('img'); if (!img) return
    let w = wrap.clientWidth
    if (w > containerW) {
      w = containerW
      const wAttr = parseInt(img.getAttribute('width'))
      const hAttr = parseInt(img.getAttribute('height'))
      const ratio = (wAttr && hAttr) ? (wAttr / hAttr)
          : (img.naturalWidth && img.naturalHeight ? (img.naturalWidth / img.naturalHeight) : 1)

      const h = Math.round(w / (ratio || 1))
      wrap.style.width = w + 'px'
      wrap.style.height = h + 'px'
      img.style.width = w + 'px'
      img.style.height = h + 'px'

      // pendingFiles에도 반영(업로드 시 올바른 크기 전송)
      const id = wrap.dataset.id
      const p = pendingFiles.value.find(x => x.id === id)
      if (p) { p.width = w; p.height = h }
    }
  })
}

function insertFromTray(p) {
  const wrap = document.createElement('div')
  wrap.className = 'image-wrapper'
  wrap.dataset.id = p.id
  wrap.contentEditable = 'false'

  const containerW = getEditorWidth()
  const initW = containerW
  const initH = Math.round(initW / (p.ratio || 1))

  Object.assign(wrap.style, {
    display: 'block',
    position: 'relative',
    maxWidth: '100%',
    margin: '1rem auto',
    resize: 'both',
    overflow: 'auto',
    width: initW + 'px',
    height: initH + 'px',
  })

  const img = document.createElement('img')
  img.src = p.url
  img.draggable = false

  Object.assign(img.style, {
    width:  initW + 'px',
    height: initH + 'px',
    display: 'block',
    maxWidth: '100%'
  })

  const del = document.createElement('button')
  del.type = 'button'
  del.className = 'img-del'
  del.textContent = '×'
  del.onclick = (e) => { e.stopPropagation(); removePending(p.id); }; // 이벤트 버블링 방지

  const resizeHandleLeft = document.createElement('div');
  resizeHandleLeft.className = 'resize-handle resize-handle-left';

  const resizeHandleRight = document.createElement('div');
  resizeHandleRight.className = 'resize-handle resize-handle-right';

  wrap.append(img, del, resizeHandleLeft, resizeHandleRight);

  enableResizable(wrap, img, p)
  insertAtCaret(wrap)
  editor.value?.focus()
}

function enableResizable(wrap, img, p) {
  // 리사이즈 핸들 드래그 로직
  let isResizing = false;
  let startX, startWidth;

  wrap.querySelectorAll('.resize-handle').forEach(handle => {
    handle.addEventListener('mousedown', (e) => {
      e.stopPropagation(); // 에디터 클릭 이벤트 방지
      isResizing = true;
      startX = e.clientX;
      startWidth = wrap.clientWidth;

      const currentTransform = getComputedStyle(wrap).transform;
      const matrix = new DOMMatrixReadOnly(currentTransform);
      const currentTranslateX = matrix.m41;

      const onMouseMove = (moveEvent) => {
        if (!isResizing) return;
        moveEvent.preventDefault();

        let deltaX = moveEvent.clientX - startX;
        let newWidth = startWidth + deltaX;
        let newTranslateX = currentTranslateX;

        if (handle.classList.contains('resize-handle-left')) {
          newWidth = startWidth - deltaX;
          newTranslateX = currentTranslateX + deltaX;
        }

        const containerW = getEditorWidth();
        const clampedWidth = Math.max(240, Math.min(newWidth, containerW));

        if (handle.classList.contains('resize-handle-left')) {
          const diff = startWidth - clampedWidth;
          wrap.style.transform = `translateX(${currentTranslateX + diff / 2}px)`;

        } else if (handle.classList.contains('resize-handle-right')) {
          const diff = clampedWidth - startWidth;
          wrap.style.transform = `translateX(${currentTranslateX + diff / 2}px)`;
        }

        const h = Math.round(clampedWidth / (p.ratio || 1));

        wrap.style.width = clampedWidth + 'px';
        wrap.style.height = h + 'px';
        img.style.width = clampedWidth + 'px';
        img.style.height = h + 'px';

        p.width = clampedWidth;
        p.height = h;
      };

      const onMouseUp = () => {
        isResizing = false;
        window.removeEventListener('mousemove', onMouseMove);
        window.removeEventListener('mouseup', onMouseUp);
      };

      window.addEventListener('mousemove', onMouseMove);
      window.addEventListener('mouseup', onMouseUp);
    });
  });

  const ro = new ResizeObserver(() => {
    const containerW = getEditorWidth()
    const wantedW    = wrap.clientWidth
    if (wantedW < 10) return  // 아직 레이아웃 계산 전

    const w = Math.max(240, Math.min(wantedW, containerW))
    const h = Math.round(w / (p.ratio || 1))

    wrap.style.width = w + 'px'
    wrap.style.height = h + 'px'
    img.style.width = w + 'px'
    img.style.height = h + 'px'

    p.width  = w
    p.height = h;

    const currentMarginLeft = parseFloat(getComputedStyle(wrap).marginLeft);
    const parentWidth = wrap.parentNode ? wrap.parentNode.clientWidth : containerW;
    if (parentWidth && wrap.clientWidth < parentWidth) {
      const newTransformX = (parentWidth - wrap.clientWidth) / 2;
      wrap.style.transform = `translateX(${newTransformX - currentMarginLeft}px)`;
    } else {
      wrap.style.transform = 'none';
    }
  });
  ro.observe(wrap)
}
async function buildHtmlWithUploadedImages () {
  const idToUrl = {};

  await Promise.all(
      pendingFiles.value.map(async (p) => {
        const form = new FormData();
        form.append('file',   p.file);
        form.append('width',  p.width || 0);
        form.append('height', p.height || 0);
        try {
          const res = await fetch('/api/upload-images', { method: 'POST', body: form });
          const { url } = await res.json();
          idToUrl[p.id] = url; // p.id === img[data-id] 여야 함!
        } catch (e) {
          console.error('이미지 업로드 실패', e);
        }
      })
  );
  const clone = editor.value.cloneNode(true);

  clone.querySelectorAll('.image-wrapper').forEach((wrap) => {
    const img = wrap.querySelector('img');
    const id = wrap.dataset.id;
    const s3Url = idToUrl[id];

    if (!img || !id || !s3Url) {
      wrap.remove();
      return;
    }

    const renderedW = Math.round(img.clientWidth || parseInt(img.style.width) || img.naturalWidth || 0);
    const ratio = (pendingFiles.value.find(p => p.id === id)?.ratio) || (img.naturalWidth ? img.naturalWidth / (img.naturalHeight || 1) : 0);
    const renderedH = renderedW && ratio ? Math.round(renderedW / ratio) : (img.naturalHeight || 0);

    const clean = document.createElement('img');
    clean.src = s3Url;
    if (renderedW)  clean.setAttribute('width', renderedW);
    if (renderedH)  clean.setAttribute('height', renderedH);
    clean.setAttribute('loading', 'lazy');
    clean.style.maxWidth = '100%';
    clean.style.display = 'block';
    clean.style.margin = '0 auto';
    wrap.replaceWith(clean);
  });

  return clone.innerHTML;
}

async function submitForm() {
  if (isSubmitting.value) return
  isSubmitting.value = true

  try {
    if (!category.value || !title.value || editor.value.innerText.trim() === '') {
      alert('필수 항목을 입력하세요')
      return
    }

    const descriptionHtml = await buildHtmlWithUploadedImages()
    console.log('[DEBUG] HTML to send:\n', descriptionHtml)
    const feedBlob = new Blob([
      JSON.stringify({
        title: title.value,
        category: category.value,
        description: descriptionHtml,
        username: store.username
      })
    ], {type: 'application/json'})

    const form = new FormData()
    form.append('feed', feedBlob)

    const token = localStorage.getItem('token')
    const res = await fetch('/api/post', {
      method: 'POST',
      body: form,
      headers: {   Authorization: `Bearer ${token}` }
    })
    if (!res.ok) {
      const error = await res.json()
      if (res.status === 401 && error.code === 'INVALID_TOKEN') {
        alert('로그인이 필요합니다!')
        router.push('/login')
        return
      } else {
        throw new Error(error.message || '알 수 없는 오류 발생')
      }
    }

    alert('작성 완료')
    const data = await res.json()
    router.push('/')
  } catch (e) {
    console.error(e)
    alert('작성 실패: ' + e.message)
  } finally {
    isSubmitting.value = false
  }
}

function goToVote() {
  router.push('/search/view/feed/vote')
}

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) return
  const r = await fetch('/api/auth/status', {headers: {Authorization: `Bearer ${token}`}})
  const d = await r.json()
  if (d.isLoggedIn && !anonymous.value) username.value = d.username
})
</script>

<style scoped>
.write-container {
  max-width: 860px;
}

.modern-write-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 40px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.03);
}

.btn-vote-soft {
  background: #eff6ff;
  color: #2563eb;
  border: none;
  padding: 8px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.9rem;
  transition: background 0.2s;
}
.btn-vote-soft:hover { background: #dbeafe; }

/* ===============================
   입력 필드 (익명, 카테고리, 제목)
================================= */
.bg-light-soft { background: #f8fafc; }

.custom-switch .form-check-input {
  width: 2.5em; height: 1.25em; cursor: pointer;
}
.custom-switch .form-check-input:checked {
  background-color: #2563eb; border-color: #2563eb;
}

.modern-input {
  background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px;
  padding: 8px 14px; font-size: 0.95rem; outline: none; transition: all 0.2s;
}
.modern-input:focus { border-color: #93c5fd; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }

.modern-select {
  appearance: none; background: #f8fafc; border: 1px solid #e2e8f0;
  border-radius: 12px; padding: 10px 16px; font-weight: 600; color: #475569;
  font-size: 0.95rem; cursor: pointer; outline: none; transition: all 0.2s;
}
.modern-select:focus { background: #ffffff; border-color: #93c5fd; }

/* 노션 스타일 제목 입력 */
.title-input {
  width: 100%; border: none; border-bottom: 2px solid #f1f5f9;
  font-size: 2.2rem; font-weight: 800; color: #0f172a; padding: 10px 0;
  background: transparent; outline: none; transition: border-color 0.2s;
  letter-spacing: -0.02em;
}
.title-input:focus { border-bottom-color: #2563eb; }
.title-input::placeholder { color: #cbd5e1; font-weight: 700; }

/* ===============================
   에디터 본문
================================= */
.editor-wrapper {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  overflow: hidden;
  transition: border-color 0.2s;
}
.editor-wrapper:focus-within { border-color: #bfdbfe; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }

.editor-toolbar {
  display: flex; align-items: center; gap: 4px; padding: 8px 12px;
  background: #f8fafc; border-bottom: 1px solid #e2e8f0;
}
.toolbar-btn {
  background: transparent; border: none; width: 36px; height: 36px;
  border-radius: 8px; color: #475569; font-size: 1.1rem;
  display: flex; align-items: center; justify-content: center; transition: all 0.15s;
}
.toolbar-btn:hover { background: #e2e8f0; color: #0f172a; }
.toolbar-divider { width: 1px; height: 20px; background: #cbd5e1; margin: 0 4px; }

.editor-content-area { padding: 24px; min-height: 350px; background: #ffffff; }

.content-editor {
  min-height: 300px; outline: none; font-size: 1.05rem; line-height: 1.7; color: #334155;
}

.editor-placeholder {
  position: absolute; top: 24px; left: 24px; color: #94a3b8; pointer-events: none; font-size: 1.05rem;
}

/* ===============================
   툴바 이미지 카운트 뱃지
================================= */
.img-count-badge {
  font-size: 0.78rem; font-weight: 600; color: #2563eb;
  background: #eff6ff; border-radius: 8px; padding: 2px 8px;
}
.toolbar-btn:disabled { opacity: 0.4; cursor: not-allowed; }

/* 에디터 드래그 오버 상태 */
.editor-content-area.drag-over {
  background: #eff6ff;
  outline: 2px dashed #93c5fd;
  outline-offset: -4px;
}
.placeholder-img-hint {
  display: block; font-size: 0.85rem; color: #b0bec5; margin-top: 6px;
}

/* ===============================
   본문 내 삽입된 이미지 리사이즈 UI
================================= */
:deep(.image-wrapper) {
  max-width: 100%; display: block; margin: 1.5rem auto; position: relative;
  border: 2px solid transparent; border-radius: 8px; transition: border-color 0.2s;
}
:deep(.image-wrapper:hover) { border-color: #bfdbfe; }
:deep(.image-wrapper img) { border-radius: 8px; }

/* 모던 리사이즈 핸들 (둥근 알약 형태) */
:deep(.resize-handle) {
  position: absolute; width: 8px; height: 32px; background: #2563eb; border-radius: 4px;
  top: 50%; transform: translateY(-50%); opacity: 0; transition: opacity 0.2s; z-index: 5;
}
:deep(.image-wrapper:hover .resize-handle) { opacity: 1; }
:deep(.resize-handle-left) { left: -4px; cursor: ew-resize; }
:deep(.resize-handle-right) { right: -4px; cursor: ew-resize; }

/* 본문 내 이미지 삭제 버튼 */
:deep(.img-del) {
  position: absolute; top: 12px; right: 12px; width: 28px; height: 28px;
  border: none; border-radius: 50%; background: rgba(0,0,0,0.6); color: #fff;
  display: flex; align-items: center; justify-content: center; opacity: 0;
  transition: opacity 0.2s; z-index: 6; font-size: 1rem; cursor: pointer;
}
:deep(.image-wrapper:hover .img-del) { opacity: 1; }
:deep(.img-del:hover) { background: #ef4444; }

/* ===============================
   하단 등록 버튼
================================= */
.btn-submit-glow {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff; border: none; padding: 14px; border-radius: 14px;
  font-size: 1.1rem; font-weight: 700; cursor: pointer;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.3);
  transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.btn-submit-glow:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 24px rgba(37, 99, 235, 0.4); }
.btn-submit-glow:disabled { background: #94a3b8; box-shadow: none; cursor: not-allowed; }


@media (max-width: 576px) {
  .modern-write-card { padding: 20px; border-radius: 16px; }
  .title-input { font-size: 1.6rem; }
}


</style>
