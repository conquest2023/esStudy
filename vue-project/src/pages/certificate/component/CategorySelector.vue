<template>
  <div class="dropdown-group mb-3">
    <!-- 대분류 -->
    <div class="dropdown">
      <button
          class="btn btn-light dropdown-toggle custom-category-btn"
          type="button"
          data-bs-toggle="dropdown"
      >
        {{ selectedMajor || "카테고리를 선택하세요" }}
      </button>
      <ul class="dropdown-menu custom-dropdown-menu">
        <li v-for="(subs, major) in categoryMapping" :key="major">
          <a href="#" class="dropdown-item" @click.prevent="selectMajor(major)">
            {{ major }}
          </a>
        </li>
      </ul>
    </div>

    <!-- 중분류 -->
    <div class="dropdown">
      <button
          class="btn btn-light dropdown-toggle custom-category-btn"
          type="button"
          data-bs-toggle="dropdown"
          :disabled="!selectedMajor"
      >
        {{ selectedMiddle || "중분류 선택" }}
      </button>
      <ul class="dropdown-menu custom-dropdown-menu">
        <li v-for="middle in middleCategories" :key="middle">
          <a href="#" class="dropdown-item" @click.prevent="selectMiddle(middle)">
            {{ middle }}
          </a>
        </li>
      </ul>
    </div>

    <!-- 소분류 -->
    <div class="dropdown">
      <button
          class="btn btn-light dropdown-toggle custom-category-btn"
          type="button"
          data-bs-toggle="dropdown"
          :disabled="!selectedMiddle"
      >
        {{ selectedSub || "자격증 선택" }}
      </button>
      <ul class="dropdown-menu custom-dropdown-menu">
        <li v-for="cert in subCategories" :key="cert">
          <a href="#" class="dropdown-item" @click.prevent="selectSub(cert)">
            {{ cert }}
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'

const emit = defineEmits(['subCategorySelected'])

const selectedMajor = ref('')
const selectedMiddle = ref('')
const selectedSub = ref('')

const middleCategories = ref([])
const subCategories = ref([])

const categoryMapping = {
  "기계": ["기계장비설비.설치", "기계제작", "금형.공작기계", "자동차", "철도"],
  "건설": ["건축", "도시.교통", "토목", "조경", "건설배관"],
  "안전관리": ["안전관리"],
  "전기.전자": ["전기", "전자"],
  "환경.에너지": ["환경", "에너지.기상"],
  "IT": ["정보기술", "민간자격증"],
  "재료": ["용접", "금속.재료", "단조.주조"],
  "식품.가공": ["식품", "제과.제빵"],
  "경영.회계.사무": ["생산관리", "경영"],
  "이용.숙박.여행.오락.스포츠": ["이용.미용", "숙박.여행.오락.스포츠"],
  "농림어업": ["임업", "어업", "농업", "축산"],
  "보건.의료": ["보건.의료"],
  "사회복지.종교": ["사회복지.종교"],
  "영업.판매": ["영업.판매"],
  "화학": ["화공", "위험물"],
  "영어": ["토익", "오픽"]
}

function selectMajor(major) {
  selectedMajor.value = major
  selectedMiddle.value = ''
  selectedSub.value = ''
  middleCategories.value = categoryMapping[major]
  subCategories.value = []
}

function selectMiddle(middle) {
  selectedMiddle.value = middle
  selectedSub.value = ''
  subCategories.value = []

  fetchSubCategories(selectedMajor.value, middle)
}

async function fetchSubCategories(major, middle) {
  const token = localStorage.getItem('token')
  try {
    const { data } = await axios.get(`/certificate/category/${major}/${middle}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    subCategories.value = data.certSchedule || []
  } catch (e) {
    console.error('소분류 불러오기 실패', e)
  }
}

function selectSub(cert) {
  selectedSub.value = cert
  emit('subCategorySelected', cert)
}
</script>

<style scoped>
.dropdown-group {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.custom-category-btn {
  flex: 1;
  height: 55px;
  font-size: 1.3rem;
  text-align: left;
  border: 1px solid #ced4da;
  background-color: white;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
.custom-dropdown-menu {
  max-height: 250px;
  overflow-y: auto;
  width: 100%;
}
</style>
