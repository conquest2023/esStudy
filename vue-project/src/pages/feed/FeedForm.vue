<template>
  <div class="container-fluid py-4">
    <form @submit.prevent="submitForm" enctype="multipart/form-data">
      <div class="row justify-content-center">
        <div class="col-lg-8 col-md-10">
          <div class="form-container p-4 bg-white shadow rounded">
            <h3 class="mb-3 d-flex justify-content-between">
              글쓰기
              <button type="button" class="btn btn-outline-primary btn-sm" @click="goToVote">+ 투표 작성하기</button>
            </h3>

            <div class="mb-3">
              <input type="checkbox" id="anonymous" v-model="anonymous" />
              <label for="anonymous">익명으로 작성하기</label>
              <div v-if="anonymous" class="mt-2">
                <input type="text" class="form-control" v-model="username" placeholder="이름을 입력하세요" required />
              </div>
            </div>

            <div class="mb-3">
              <select v-model="category" class="form-select" required>
                <option disabled value="">게시판을 선택해 주세요</option>
                <option v-for="opt in categories" :key="opt" :value="opt">{{ opt }}</option>
              </select>
            </div>

            <input type="text" v-model="title" class="form-control mb-3 title-input" placeholder="제목을 입력해 주세요" required />

            <div class="content-container">
              <div v-if="showPlaceholder" class="content-placeholder">내용을 입력하세요.</div>
              <div
                  class="content-editor"
                  contenteditable="true"
                  ref="editor"
                  @input="onEditorInput"
                  @focus="showPlaceholder = false"
                  @blur="checkEditorEmpty"
              ></div>
            </div>

            <div class="mt-3">
              <label class="form-label">이미지 파일 업로드</label>
              <div class="image-upload-container" @click="$refs.imageInput.click()">
                <label><i class="fas fa-upload"></i> 클릭하여 사진을 업로드하세요</label>
                <input ref="imageInput" type="file" class="d-none" accept="image/*" multiple @change="handleFiles" />
              </div>

<!--              <div v-for="(img, index) in imagePreviews" :key="index" class="image-preview-wrapper">-->
<!--                <img :src="img" />-->
<!--                <button type="button" @click="removeImage(index)">&times;</button>-->
<!--              </div>-->
            </div>

            <button type="submit" class="btn btn-success mt-4">
              <i class="fas fa-pen"></i> 작성하기
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

  const router = useRouter()
  const anonymous = ref(false)
  const username = ref('')
  const category = ref('')
  const title = ref('')
  const content = ref('')
  const categories = ['자유', '자격증', '문제', '기술', '취업', 'Q/A', '자료']
  const showPlaceholder = ref(true)
  const editor = ref(null)
  const imageFiles = ref([])
  const imagePreviews = ref([])

  function goToVote() {
    router.push('/search/view/feed/vote')
  }

  function checkEditorEmpty() {
    showPlaceholder.value = editor.value.innerText.trim() === ''
  }

  function onEditorInput() {
    showPlaceholder.value = editor.value.innerText.trim() === ''
    content.value = editor.value.innerHTML
  }

  function handleFiles(e) {
    const files = Array.from(e.target.files)
    imageFiles.value.push(...files)

    files.forEach(file => {
      const reader = new FileReader()
      reader.onload = (e) => {
        const img = document.createElement('img')
        img.src = e.target.result
        img.style.maxWidth = '100%'
        img.style.margin = '10px 0'
        img.style.display = 'block'

        const wrapper = document.createElement('div')
        wrapper.className = 'image-wrapper'
        wrapper.contentEditable = false
        wrapper.style.position = 'relative'
        wrapper.style.display = 'inline-block'
        wrapper.style.resize = 'both'
        wrapper.style.overflow = 'auto'

        wrapper.appendChild(img)

        const deleteBtn = document.createElement('button')
        deleteBtn.innerHTML = '&times;'
        deleteBtn.style.position = 'absolute'
        deleteBtn.style.top = '5px'
        deleteBtn.style.right = '5px'
        deleteBtn.style.background = 'red'
        deleteBtn.style.color = 'white'
        deleteBtn.style.border = 'none'
        deleteBtn.style.borderRadius = '50%'
        deleteBtn.style.width = '25px'
        deleteBtn.style.height = '25px'
        deleteBtn.style.cursor = 'pointer'
        deleteBtn.onclick = () => wrapper.remove()

        wrapper.appendChild(deleteBtn)

        editor.value.appendChild(wrapper)
        editor.value.appendChild(document.createElement('br'))
      }
      reader.readAsDataURL(file)
    })
  }

function getCleanedEditorHTML() {
  const clone = editor.value.cloneNode(true)

  clone.querySelectorAll('.image-wrapper').forEach(wrapper => {
    const img = wrapper.querySelector('img')
    if (img) {
      const cleanImg = document.createElement('img')
      cleanImg.src = img.src
      cleanImg.style.width = wrapper.offsetWidth + 'px'
      cleanImg.style.height = wrapper.offsetHeight + 'px'
      cleanImg.style.maxWidth = '100%'
      wrapper.replaceWith(cleanImg)
    }
  })

  return clone.innerHTML
}



function removeImage(index) {
    imageFiles.value.splice(index, 1)
    imagePreviews.value.splice(index, 1)
  }

  async function submitForm() {
    if (!category.value || !title.value || !editor.value.innerText.trim()) {
      alert('모든 필드를 입력해주세요')
      return
    }

    const formData = new FormData()

    const editorClone = editor.value.cloneNode(true)
    editorClone.querySelectorAll('.image-wrapper').forEach(wrapper => {
      const img = wrapper.querySelector('img')
      if (img) {
        const cleanImg = document.createElement('img')
        cleanImg.src = img.src
        cleanImg.width = wrapper.offsetWidth
        cleanImg.height = wrapper.offsetHeight
        cleanImg.style.maxWidth = '100%'
        wrapper.parentNode.replaceChild(cleanImg, wrapper)
      }
    })

    formData.append('feed', new Blob([JSON.stringify({
      title: title.value,
      category: category.value,
      description: editorClone.innerHTML,
      username: anonymous.value ? username.value : username.value.trim()
    })], { type: 'application/json' }))

    imageFiles.value.forEach(file => formData.append('imageFiles', file))

    try {
      const token = localStorage.getItem('token')
      const res = await fetch('/api/search/view/feed/save', {
        method: 'POST',
        body: formData,
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = await res.json()
      console.log(data)
      if (data.success) {
        alert("게시글이 성공적으로 작성되셨습니다.")
        router.push("/")
      } else {
        throw new Error(data.error || '등록 실패')
      }
    } catch (e) {
      console.error(e)
      alert('작성 실패')
    }
  }

onMounted(async () => {
    const token = localStorage.getItem('token')
    if (!token) return

    try {
      const res = await fetch('/api/auth/status', {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.isLoggedIn) {
        if (!anonymous.value) {
          username.value = data.username
        }
      }
    } catch (e) {
      console.error('유저 정보 불러오기 실패:', e)
    }
  })

</script>

<style scoped>
  .title-input {
    font-size: 1.1rem;
    border-bottom: 2px solid #ccc;
    border-radius: 0;
  }
  .content-container {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    min-height: 200px;
    position: relative;
  }
  .content-placeholder {
    position: absolute;
    color: #aaa;
    pointer-events: none;
  }
  .content-editor {
    min-height: 200px;
    outline: none;
  }
  .image-upload-container {
    border: 2px dashed #ccc;
    text-align: center;
    padding: 10px;
    border-radius: 5px;
    background: #f8f9fa;
    cursor: pointer;
  }
  .image-wrapper {
    resize: both;
    overflow: auto;
    display: inline-block;
    position: relative;
    margin: 10px 0;
    max-width: 100%;
  }

  .image-wrapper img {
    width: 400px;
    max-width: 100%;
    border: 1px dashed #ccc;
    display: block;
  }

  .delete-btn {
    position: absolute;
    top: 0;
    right: 0;
    background: red;
    color: white;
    border: none;
    border-radius: 50%;
    width: 25px;
    height: 25px;
    cursor: pointer;
  }

  .image-preview-wrapper img {
    width: 100%;
    max-width: 400px;
    border: 1px dashed #ccc;
  }
  .image-preview-wrapper button {
    position: absolute;
    top: 0;
    right: 0;
    background: red;
    color: white;
    border: none;
    border-radius: 50%;
    width: 25px;
    height: 25px;
    cursor: pointer;
  }
</style>
