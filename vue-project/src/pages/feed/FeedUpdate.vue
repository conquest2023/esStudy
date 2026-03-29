<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'

const route  = useRoute()
const router = useRouter()
const feedId = route.query.id

const curTitle          = ref('')
const curDescriptionHtml = ref('')
const title             = ref('')
const loading           = ref(false)
const fetching          = ref(true)
const errorMsg          = ref('')
const showPlaceholder   = ref(true)
const editor            = ref(null)
const imageInput        = ref(null)
const isDragOver        = ref(false)
const pendingFiles      = ref([])
const MAX_IMAGES        = 3

onMounted(async () => {
  try {
    const { data } = await api.get(`/post/${feedId}`)
    curTitle.value           = data.ok.post.title ?? ''
    curDescriptionHtml.value = data.ok.post.description ?? ''
    title.value              = curTitle.value
  } catch (err) {
    console.error(err)
    errorMsg.value = '게시글을 불러오지 못했습니다.'
  } finally {
    fetching.value = false
    await nextTick()
    if (editor.value && curDescriptionHtml.value) {
      editor.value.innerHTML = curDescriptionHtml.value
      showPlaceholder.value  = false
    }
  }

  _onResize = () => clampImagesToContainer()
  window.addEventListener('resize', _onResize)
})

let _onResize
onBeforeUnmount(() => window.removeEventListener('resize', _onResize))

const onEditorDragOver  = () => { isDragOver.value = true }
const onEditorDragLeave = () => { isDragOver.value = false }
const onEditorDrop      = (e) => {
  isDragOver.value = false
  const files = e.dataTransfer?.files
  if (files?.length) handleFiles(files)
}
const checkEditorEmpty = () => (showPlaceholder.value = !editor.value?.innerText.trim())
const onEditorInput    = () => checkEditorEmpty()

async function handleFiles(e) {
  const list   = e.target?.files ? Array.from(e.target.files) : Array.from(e)
  const remain = MAX_IMAGES - pendingFiles.value.length
  if (remain <= 0) { alert(`이미지는 최대 ${MAX_IMAGES}장까지 추가할 수 있어요.`); resetChooser(); return }
  const chosen = list.slice(0, remain)
  if (list.length > remain) alert(`최대 ${MAX_IMAGES}장까지 가능: ${remain}장만 추가됩니다.`)

  for (const file of chosen) {
    if (!file.type.startsWith('image/')) continue
    const id  = crypto.randomUUID()
    const url = URL.createObjectURL(file)
    const img = new Image()
    await new Promise(r => { img.onload = r; img.src = url })
    const p = { id, file, url, width: 480, height: Math.round(480 * img.naturalHeight / img.naturalWidth), ratio: img.naturalWidth / img.naturalHeight }
    pendingFiles.value.push(p)
    await nextTick()
    insertImage(p)
  }
  resetChooser()
}

function resetChooser() {
  if (imageInput.value) imageInput.value.value = ''
}

function getEditorWidth() {
  const el = editor.value
  if (!el) return 720
  return Math.max(320, Math.min(720, Math.round(el.getBoundingClientRect().width - 28)))
}

function insertAtCaret(node) {
  editor.value?.appendChild(node)
  editor.value?.appendChild(document.createElement('br'))
  const sel = window.getSelection()
  if (sel) {
    const range = document.createRange()
    range.setStartAfter(node)
    range.collapse(true)
    sel.removeAllRanges()
    sel.addRange(range)
  }
}

function insertImage(p) {
  const containerW = getEditorWidth()
  const initW      = containerW
  const initH      = Math.round(initW / (p.ratio || 1))

  const wrap = document.createElement('div')
  wrap.className       = 'image-wrapper'
  wrap.dataset.id      = p.id
  wrap.contentEditable = 'false'
  Object.assign(wrap.style, {
    display: 'block', margin: '1rem auto', position: 'relative',
    maxWidth: '100%', resize: 'both', overflow: 'auto',
    width: initW + 'px', height: initH + 'px',
  })

  const img = document.createElement('img')
  img.src       = p.url
  img.draggable = false
  Object.assign(img.style, { width: initW + 'px', height: initH + 'px', display: 'block', maxWidth: '100%' })

  const del       = document.createElement('button')
  del.type        = 'button'
  del.className   = 'img-del'
  del.textContent = '×'
  del.onclick     = (e) => { e.stopPropagation(); removeImage(p.id) }

  const handleL = document.createElement('div')
  handleL.className = 'resize-handle resize-handle-left'
  const handleR = document.createElement('div')
  handleR.className = 'resize-handle resize-handle-right'

  wrap.append(img, del, handleL, handleR)
  enableResizable(wrap, img, p)
  insertAtCaret(wrap)
  editor.value?.focus()
}

function removeImage(id) {
  editor.value?.querySelector(`.image-wrapper[data-id="${id}"]`)?.remove()
  const idx = pendingFiles.value.findIndex(p => p.id === id)
  if (idx >= 0) { URL.revokeObjectURL(pendingFiles.value[idx].url); pendingFiles.value.splice(idx, 1) }
}

function clampImagesToContainer() {
  const containerW = getEditorWidth()
  editor.value?.querySelectorAll('.image-wrapper').forEach(wrap => {
    const img = wrap.querySelector('img'); if (!img) return
    let w = wrap.clientWidth
    if (w > containerW) {
      w = containerW
      const ratio = img.naturalWidth && img.naturalHeight ? img.naturalWidth / img.naturalHeight : 1
      const h     = Math.round(w / ratio)
      wrap.style.width = w + 'px'; wrap.style.height = h + 'px'
      img.style.width  = w + 'px'; img.style.height  = h + 'px'
      const pf = pendingFiles.value.find(x => x.id === wrap.dataset.id)
      if (pf) { pf.width = w; pf.height = h }
    }
  })
}

function enableResizable(wrap, img, p) {
  let isResizing = false, startX, startWidth

  wrap.querySelectorAll('.resize-handle').forEach(handle => {
    handle.addEventListener('mousedown', (e) => {
      e.stopPropagation()
      isResizing = true; startX = e.clientX; startWidth = wrap.clientWidth

      const onMouseMove = (mv) => {
        if (!isResizing) return
        mv.preventDefault()
        const deltaX  = mv.clientX - startX
        const newW    = handle.classList.contains('resize-handle-left') ? startWidth - deltaX : startWidth + deltaX
        const clamped = Math.max(240, Math.min(newW, getEditorWidth()))
        const h       = Math.round(clamped / (p.ratio || 1))
        wrap.style.width = clamped + 'px'; wrap.style.height = h + 'px'
        img.style.width  = clamped + 'px'; img.style.height  = h + 'px'
        p.width = clamped; p.height = h
      }
      const onMouseUp = () => { isResizing = false; window.removeEventListener('mousemove', onMouseMove); window.removeEventListener('mouseup', onMouseUp) }
      window.addEventListener('mousemove', onMouseMove)
      window.addEventListener('mouseup', onMouseUp)
    })
  })

  const ro = new ResizeObserver(() => {
    const containerW = getEditorWidth()
    const wantedW    = wrap.clientWidth
    if (wantedW < 10) return
    const w = Math.max(240, Math.min(wantedW, containerW))
    const h = Math.round(w / (p.ratio || 1))
    wrap.style.width = w + 'px'; wrap.style.height = h + 'px'
    img.style.width  = w + 'px'; img.style.height  = h + 'px'
    p.width = w; p.height = h
  })
  ro.observe(wrap)
}

async function buildHtml() {
  const idToUrl = {}
  await Promise.all(pendingFiles.value.map(async (p) => {
    const form = new FormData()
    form.append('file', p.file); form.append('width', p.width || 0); form.append('height', p.height || 0)
    try {
      const res = await fetch('/api/upload-images', { method: 'POST', body: form })
      const { url } = await res.json()
      idToUrl[p.id] = url
    } catch (e) { console.error('이미지 업로드 실패', e) }
  }))

  const clone = editor.value.cloneNode(true)
  clone.querySelectorAll('.image-wrapper').forEach((wrap) => {
    const img = wrap.querySelector('img'); const id = wrap.dataset.id; const s3Url = idToUrl[id]
    if (!img || !id || !s3Url) { wrap.remove(); return }
    const renderedW = Math.round(img.clientWidth || parseInt(img.style.width) || 0)
    const ratio     = pendingFiles.value.find(pf => pf.id === id)?.ratio || 1
    const clean = document.createElement('img')
    clean.src = s3Url
    if (renderedW) { clean.setAttribute('width', renderedW); clean.setAttribute('height', Math.round(renderedW / ratio)) }
    clean.setAttribute('loading', 'lazy')
    clean.style.cssText = 'max-width:100%;display:block;margin:0 auto;'
    wrap.replaceWith(clean)
  })
  return clone.innerHTML
}

async function submitEdit() {
  if (!title.value.trim() || !editor.value?.innerText.trim()) {
    errorMsg.value = '제목과 내용을 입력해주세요.'; return
  }
  loading.value = true; errorMsg.value = ''
  try {
    const description = await buildHtml()
    const token = localStorage.getItem('token')
    await api.put(`/post/${feedId}`, { id: feedId, title: title.value, description }, { headers: { Authorization: `Bearer ${token}` } })
    router.push(`/post/${feedId}`)
  } catch (e) {
    console.error(e); errorMsg.value = '수정 중 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}

function resetToCurrent() {
  title.value = curTitle.value
  pendingFiles.value.forEach(p => URL.revokeObjectURL(p.url))
  pendingFiles.value = []
  if (editor.value) { editor.value.innerHTML = curDescriptionHtml.value; showPlaceholder.value = !curDescriptionHtml.value }
}

function goBack() { router.back() }
</script>

<template>
  <div class="container py-5" style="max-width: 880px">
    <h2 class="fw-bold mb-4 text-center">게시글 수정</h2>

    <div v-if="errorMsg" class="alert alert-danger">{{ errorMsg }}</div>

    <div v-if="fetching" class="text-center py-5">
      <div class="spinner-border" />
    </div>

    <template v-else>
      <!-- 현재 글 미리보기 -->
      <div class="card shadow-sm mb-4">
        <div class="card-header d-flex justify-content-between align-items-center">
          <strong>현재 게시글</strong>
          <button class="btn btn-sm btn-outline-secondary" @click="goBack">이전으로</button>
        </div>
        <div class="card-body">
          <h4 class="mb-3">{{ curTitle || '제목 없음' }}</h4>
          <div class="text-muted small mb-2">현재 게시글 내용</div>
          <div class="preview-content" v-html="curDescriptionHtml || '<em>내용 없음</em>'"></div>
        </div>
      </div>

      <div class="text-center my-3">
        <span class="badge rounded-pill text-bg-light">아래에서 수정</span>
      </div>

      <!-- 수정 폼 -->
      <div class="card shadow-sm">
        <div class="card-body">
          <div class="form-floating mb-3">
            <input v-model="title" type="text" class="form-control" id="titleInput" placeholder="제목" />
            <label for="titleInput">수정할 제목</label>
          </div>

          <!-- 에디터 -->
          <div class="editor-wrapper mb-4">
            <div class="editor-toolbar">
              <button type="button" class="toolbar-btn" @click="$refs.imageInput.click()" :disabled="pendingFiles.length >= MAX_IMAGES" title="이미지 삽입">
                <i class="bi bi-image" />
              </button>
              <span v-if="pendingFiles.length > 0" class="img-count-badge">{{ pendingFiles.length }}/{{ MAX_IMAGES }}</span>
              <input ref="imageInput" type="file" accept="image/*" multiple class="d-none" @change="handleFiles" />
            </div>
            <div
              class="editor-content-area position-relative"
              @dragover.prevent="onEditorDragOver"
              @dragleave="onEditorDragLeave"
              @drop.prevent="onEditorDrop"
              :class="{ 'drag-over': isDragOver }"
            >
              <p v-if="showPlaceholder" class="editor-placeholder">
                내용을 입력하세요...<br>
                <span class="placeholder-hint"><i class="bi bi-image me-1"></i>이미지를 드래그하거나 위 버튼으로 삽입하세요</span>
              </p>
              <div ref="editor" class="content-editor" contenteditable @input="onEditorInput" @focus="showPlaceholder = false" @blur="checkEditorEmpty"></div>
            </div>
          </div>

          <div class="d-flex gap-2">
            <button class="btn btn-outline-secondary" type="button" @click="resetToCurrent">현재 내용으로 되돌리기</button>
            <button class="btn btn-primary ms-auto" :disabled="loading" @click="submitEdit">
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              수정 완료
            </button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.preview-content { line-height: 1.7; white-space: pre-line; word-break: break-word; }

.editor-wrapper { border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden; }
.editor-wrapper:focus-within { border-color: #86b7fe; box-shadow: 0 0 0 0.2rem rgba(13,110,253,.25); }

.editor-toolbar {
  display: flex; align-items: center; gap: 6px; padding: 8px 12px;
  background: #f8f9fa; border-bottom: 1px solid #dee2e6;
}
.toolbar-btn {
  background: transparent; border: none; width: 34px; height: 34px;
  border-radius: 6px; color: #495057; font-size: 1.1rem;
  display: flex; align-items: center; justify-content: center; transition: background 0.15s;
}
.toolbar-btn:hover { background: #e9ecef; }
.toolbar-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.img-count-badge { font-size: 0.78rem; font-weight: 600; color: #0d6efd; background: #e7f1ff; border-radius: 6px; padding: 2px 8px; }

.editor-content-area { padding: 16px; min-height: 240px; background: #fff; }
.editor-content-area.drag-over { background: #f0f7ff; outline: 2px dashed #86b7fe; outline-offset: -4px; }

.content-editor { min-height: 200px; outline: none; font-size: 1rem; line-height: 1.7; color: #212529; }

.editor-placeholder { position: absolute; top: 16px; left: 16px; color: #adb5bd; pointer-events: none; font-size: 1rem; margin: 0; }
.placeholder-hint { display: block; font-size: 0.82rem; color: #ced4da; margin-top: 4px; }

/* 본문 내 삽입된 이미지 */
:deep(.image-wrapper) { max-width: 100%; display: block; margin: 1rem auto; position: relative; border: 2px solid transparent; border-radius: 6px; transition: border-color 0.2s; }
:deep(.image-wrapper:hover) { border-color: #86b7fe; }
:deep(.image-wrapper img) { border-radius: 6px; }

:deep(.resize-handle) { position: absolute; width: 8px; height: 28px; background: #0d6efd; border-radius: 4px; top: 50%; transform: translateY(-50%); opacity: 0; transition: opacity 0.2s; z-index: 5; }
:deep(.image-wrapper:hover .resize-handle) { opacity: 1; }
:deep(.resize-handle-left) { left: -4px; cursor: ew-resize; }
:deep(.resize-handle-right) { right: -4px; cursor: ew-resize; }

:deep(.img-del) { position: absolute; top: 8px; right: 8px; width: 26px; height: 26px; border: none; border-radius: 50%; background: rgba(0,0,0,0.55); color: #fff; display: flex; align-items: center; justify-content: center; opacity: 0; transition: opacity 0.2s; z-index: 6; font-size: 1rem; cursor: pointer; }
:deep(.image-wrapper:hover .img-del) { opacity: 1; }
:deep(.img-del:hover) { background: #dc3545; }

input.form-control:focus { border-color: #86b7fe; box-shadow: 0 0 0 0.2rem rgba(13,110,253,.25); }
</style>
