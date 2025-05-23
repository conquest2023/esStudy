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

      <div
          class="upload-dropzone mb-4"
          @dragover.prevent
          @drop.prevent="filesDropped($event.dataTransfer.files)"
          @click="$refs.imageInput.click()"
      >
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

      <button class="btn btn-success w-100 py-2 fs-6">
        <i class="bi bi-pencil-square me-1"></i> 작성하기
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router           = useRouter()
const anonymous        = ref(false)
const username         = ref('')
const category         = ref('')
const title            = ref('')
const categories       = ['자유', '자격증', '문제', '기술', '취업', 'Q/A', '자료']
const showPlaceholder  = ref(true)
const editor           = ref(null)
const pendingFiles     = ref([])

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

function filesDropped (files) { handleFiles({ target: { files } }) }
function handleFiles (e) {
  const files = Array.from(e.target.files)
  files.forEach(file => {
    const id  = crypto.randomUUID()
    const url = URL.createObjectURL(file)

    const img = document.createElement('img')
    img.src = url
    img.dataset.id = id
    Object.assign(img.style, { maxWidth: '100%', margin: '10px 0', display: 'block' })

    const wrap = document.createElement('div')
    wrap.className = 'image-wrapper'
    wrap.dataset.id = id
    Object.assign(wrap.style, {
      position: 'relative', display: 'inline-block',
      resize: 'both', overflow: 'auto', maxWidth: '100%'
    })
    wrap.contentEditable = 'false'

    const del = document.createElement('button')
    del.innerHTML = '&times;'
    Object.assign(del.style, {
      position: 'absolute', top: '5px', right: '5px', width: '26px', height: '26px',
      background: '#dc3545', color: '#fff', border: 'none', borderRadius: '50%', cursor: 'pointer'
    })
    del.onclick = () => {
      wrap.remove()
      pendingFiles.value = pendingFiles.value.filter(p => p.id !== id)
    }

    wrap.append(img, del)
    editor.value.append(wrap, document.createElement('br'))

    pendingFiles.value.push({ id, file })
  })
}

async function buildHtmlWithUploadedImages () {
  const idToUrl = {}
  for (const { id, file } of pendingFiles.value) {
    const form = new FormData()
    form.append('file', file)
    try {
      const res = await fetch('/api/upload-images', {
        method: 'POST',
        body: form
      })
      const { url } = await res.json()
      idToUrl[id] = url
    } catch (err) {
      console.error('이미지 업로드 실패', err)
    }
  }

  const clone = editor.value.cloneNode(true)
  clone.querySelectorAll('.image-wrapper').forEach(w => {
    const id = w.dataset.id
    const img = w.querySelector('img')
    if (!img) return

    const clean = img.cloneNode(false)
    clean.src = idToUrl[id] || img.src
    clean.removeAttribute('data-id')
    clean.style.maxWidth = '100%'
    clean.style.width = '400px'
    clean.style.height = 'auto'

    w.replaceWith(clean)
  })

  return clone.innerHTML
}

async function submitForm() {
  if (!category.value || !title.value || editor.value.innerText.trim() === '') {
    alert('필수 항목을 입력하세요')
    return
  }

  const descriptionHtml = await buildHtmlWithUploadedImages()

  const feedBlob = new Blob([
    JSON.stringify({
      title: title.value,
      category: category.value,
      description: descriptionHtml,
      username: anonymous.value ? username.value.trim() : username.value
    })
  ], {type: 'application/json'})

  const form = new FormData()
  form.append('feed', feedBlob)

  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/search/view/feed/save', {
      method: 'POST',
      body: form,
      headers: {Authorization: `Bearer ${token}`}
    })
    const data = await res.json()
    if (data.success) {
      alert('작성 완료')
      router.push('/')
    } else {
      throw new Error(data.error)
    }
  } catch (e) {
    console.error(e)
    alert('작성 실패')
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
</style>