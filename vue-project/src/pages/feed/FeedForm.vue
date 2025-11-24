<template>
  <div class="container-xl py-5">
    <form @submit.prevent="submitForm" class="mx-auto write-card shadow-sm">
      <header class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="m-0 fw-bold">새 글 작성</h3>
        <button class="btn btn-outline-primary btn-sm" type="button" @click="goToVote">
          🗳️ 투표 작성
        </button>
      </header>

      <div class="d-flex align-items-center gap-3 mb-3">
        <div class="form-check form-switch m-0">
          <input id="anonymous" class="form-check-input" type="checkbox" v-model="anonymous" />
          <label class="form-check-label small" for="anonymous">익명</label>
        </div>
        <input
            v-if="anonymous"
            v-model="username"
            class="form-control flex-grow-1"
            placeholder="익명 닉네임"
        />
      </div>

      <div class="row g-2 mb-3">
        <div class="col-auto">
          <select v-model="category" class="form-select category-pill" required>
            <option disabled value="">카테고리</option>
            <option v-for="opt in categories" :key="opt">{{ opt }}</option>
          </select>
        </div>
        <div class="col">
          <input
              v-model="title"
              class="form-control fs-5 fw-semibold"
              placeholder="제목을 입력하세요"
              required
          />
        </div>
      </div>

      <div class="editor-wrapper mb-3">
        <div class="editor-toolbar">
          <button type="button" @click="applyFormat('bold')"><i class="bi bi-type-bold" /></button>
          <button type="button" @click="applyFormat('link')"><i class="bi bi-link-45deg" /></button>
          <button type="button" @click="applyFormat('code')"><i class="bi bi-code-slash" /></button>
        </div>

        <div
            ref="editor"
            class="content-editor"
            contenteditable
            @input="onEditorInput"
            @focus="showPlaceholder = false"
            @blur="checkEditorEmpty"
        />
        <p v-if="showPlaceholder" class="editor-placeholder">내용을 입력하세요…</p>
      </div>
      <div class="thumb-tray mb-2" v-if="pendingFiles.length">
        <div class="tray-head">
          <span>이미지 {{ pendingFiles.length }}/{{ MAX_IMAGES }}</span>
        </div>
        <div class="thumbs">
          <div class="thumb" v-for="p in pendingFiles" :key="p.id">
            <img :src="p.url" :alt="p.file?.name || 'preview'">
            <button type="button" class="thumb-del" @click="removePending(p.id)">×</button>
          </div>
        </div>
      </div>

      <div
          class="upload-dropzone mb-4"
          @dragover.prevent
          @drop.prevent="filesDropped($event.dataTransfer.files)"
          @click="$refs.imageInput.click()">
        <input
            ref="imageInput"
            type="file"
            accept="image/*"
            multiple
            class="d-none"
            @change="handleFiles"
        />
        <p class="m-0 text-muted small">클릭 또는 드래그하여 이미지 업로드</p>
      </div>

      <button class="btn btn-success w-100 py-2 fs-6" :disabled="isSubmitting">
        <span v-if="isSubmitting">
          <i class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></i>
          작성 중...
        </span>
        <span v-else>
          <i class="bi bi-pencil-square me-1"></i> 작성하기
        </span>
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {useUserStore} from "@/stores/user.js";

const router           = useRouter()
const anonymous        = ref(false)
const username         = ref('')
const category         = ref('')
const title            = ref('')
const store   = useUserStore()
const MAX_IMAGES = 3
const pendingFiles = ref([])
// 간단 토스트
function toast(msg){ alert(msg) }
const categories       = ['자유', '자격증', '문제', '기술', '취업', 'Q/A', '자료']
const showPlaceholder  = ref(true)
const editor           = ref(null)
const isSubmitting     = ref(false) // 중복 제출 방지

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



// 기존 handleFiles 교체
async function handleFiles (e) {
  const list = e.target?.files ? Array.from(e.target.files) : Array.from(e)
  const remain = MAX_IMAGES - pendingFiles.value.length
  if (remain <= 0) {
    toast(`이미지는 최대 ${MAX_IMAGES}장까지 업로드할 수 있어요.`)
    resetChooser()
    return
  }

  const chosen = list.slice(0, remain)
  if (list.length > remain) {
    toast(`최대 ${MAX_IMAGES}장까지 가능: ${remain}장만 추가됩니다.`)
  }

  for (const file of chosen) {
    if (!file.type.startsWith('image/')) continue
    const id  = crypto.randomUUID()
    const url = URL.createObjectURL(file)

    // 미리보기 이미지 (비율 고정)
    const img = document.createElement('img')
    img.src = url
    img.dataset.id = id
    Object.assign(img.style, {
      display: 'block',
      maxWidth: '100%',
      width: '480px',   // 기본 표시 폭
      height: 'auto',   // 🔑 비율 유지
      margin: '10px 0'
    })

    // 래퍼 (삭제 버튼만 유지, resize 핸들 제거)
    const wrap = document.createElement('div')
    wrap.className = 'image-wrapper'
    wrap.dataset.id = id
    wrap.contentEditable = 'false'
    Object.assign(wrap.style, {
      position: 'relative',
      display: 'block'
    })

    const del = document.createElement('button')
    del.type = 'button'
    del.textContent = '×'
    Object.assign(del.style, {
      position:'absolute', top:'4px', right:'4px',
      width:'26px', height:'26px',
      background:'#dc3545', color:'#fff', border:'none',
      borderRadius:'50%', cursor:'pointer'
    })
    del.onclick = () => removePending(id)

    wrap.append(img, del)

    // 커서 뒤에 삽입 (에디터 UX)
    insertAtCaret(wrap)
    editor.value?.append(document.createElement('br'))

    // 자연 크기 읽어 비율 저장
    await imgDecode(img)
    const natW = img.naturalWidth || 1
    const natH = img.naturalHeight || 1
    pendingFiles.value.push({
      id, file, url,
      width: img.clientWidth,
      height: img.clientHeight,
      ratio: natW / natH
    })
  }

  resetChooser() // 같은 파일 재선택 가능하게 초기화
}
function imgDecode(img){
  return img.decode ? img.decode().catch(()=>{}) : Promise.resolve()
}

// 커서 위치에 노드 삽입
function insertAtCaret(node) {
  const sel = window.getSelection()
  if (!sel || !sel.rangeCount) {
    editor.value?.appendChild(node)
    return
  }
  const range = sel.getRangeAt(0)
  range.collapse(false)
  range.insertNode(node)
  // 커서 wrap 뒤로
  range.setStartAfter(node)
  range.setEndAfter(node)
  sel.removeAllRanges()
  sel.addRange(range)
}
// 파일 입력 초기화
function resetChooser(){
  if (typeof $refs?.imageInput?.value !== 'undefined') {
    $refs.imageInput.value = ''
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

// 드래그 앤 드롭 핸들링 유지
function filesDropped (files) {
  handleFiles(files)
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
  // 2) 에디터 복제 → img[data-id]를 S3 URL로 교체
  const clone = editor.value.cloneNode(true);

  clone.querySelectorAll('img[data-id]').forEach((img) => {
    const id = img.dataset.id;
    const s3Url = idToUrl[id];

    // 원본 렌더 폭 기반 width/height 계산(가능하면 유지)
    const renderedW = Math.round(img.clientWidth || parseInt(img.style.width) || img.naturalWidth || 0);
    const ratio = (pendingFiles.value.find(p => p.id === id)?.ratio) || (img.naturalWidth ? img.naturalWidth / (img.naturalHeight || 1) : 0);
    const renderedH = renderedW && ratio ? Math.round(renderedW / ratio) : (img.naturalHeight || 0);

    const clean = document.createElement('img');
    clean.src = s3Url || img.src; // 업로드 실패 시라도 미리보기 유지
    if (renderedW)  clean.setAttribute('width', renderedW);
    if (renderedH)  clean.setAttribute('height', renderedH);
    clean.setAttribute('loading', 'lazy');
    clean.style.maxWidth = '100%';

    // data-id 등 미리보기 전용 속성 제거
    img.replaceWith(clean);
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
.write-card {
  max-width: 780px;
  background: #fff;
  border-radius: 12px;
  padding: 32px
}

.category-pill {
  padding: .25rem .8rem;
  border-radius: 50px;
  font-size: .9rem
}

.editor-wrapper {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  position: relative
}

.editor-toolbar {
  display: flex;
  gap: 8px;
  padding: 6px 8px;
  border-bottom: 1px solid #e0e0e0;
  background: #fafafa
}

.editor-toolbar button {
  border: none;
  background: none;
  font-size: 1.1rem;
  color: #555;
  cursor: pointer
}
.thumb-tray { border:1px solid #e5e7eb; border-radius:8px; padding:8px; background:#fafafa }
.tray-head { font-size:.9rem; color:#666; margin-bottom:6px; display:flex; justify-content:space-between }
.thumbs { display:flex; gap:8px; flex-wrap:wrap }
.thumb { position:relative; width:84px; height:84px; border-radius:8px; overflow:hidden; background:#fff; border:1px solid #e5e7eb }
.thumb img { width:100%; height:100%; object-fit:cover }
.thumb-del { position:absolute; top:2px; right:2px; width:22px; height:22px; border:none; border-radius:50%; background:#dc3545; color:#fff; cursor:pointer }
.content-editor {
  min-height: 230px;
  padding: 14px;
  font-size: 1rem;
  line-height: 1.6;
  outline: none
}

.editor-placeholder {
  position: absolute;
  top: 48px;
  left: 16px;
  color: #b1b1b1
}

.upload-dropzone {
  border: 2px dashed #cdd4da;
  border-radius: 6px;
  padding: 24px;
  text-align: center;
  transition: .2s
}

.upload-dropzone:hover {
  background: #f8f9fa;
  cursor: pointer
}

.image-wrapper img {
  border: 1px dashed #ced4da;
  width: 400px;
  max-width: 100%
}
.image-wrapper img,
.content-editor img {
  image-rendering: auto;
  -webkit-user-drag: none;
}

.image-wrapper img {
  max-width: 100%;
  width: 480px;
  height: auto;  /* 🔑 중요 */
  border: 1px dashed #ced4da;
}

</style>
