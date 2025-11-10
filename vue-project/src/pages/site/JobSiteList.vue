<template>
  <div class="container my-5">
    <h2 class="fw-bold text-center mb-4">인기 취업 사이트 모음</h2>

    <!-- 필터 버튼 -->
    <div class="d-flex justify-content-center mb-4 flex-wrap gap-2">
      <button
          v-for="cat in categories"
          :key="cat.key"
          class="btn"
          :class="filter === cat.key ? 'btn-primary' : cat.class"
          @click="filter = cat.key"
      >
        {{ cat.label }}
      </button>
    </div>




    <!-- 사이트 리스트 -->
    <div class="row row-cols-1 row-cols-md-3 g-4">
      <div
          v-for="site in filteredSites"
          :key="site.name"
          class="col job-site"
      >
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div class="d-flex align-items-center mb-2">
              <h5 class="card-title fw-bold mb-1">{{ site.name }}</h5>
            </div>
            <p class="card-text text-muted">{{ site.description }}</p>
            <a
                :href="site.link"
                class="btn btn-primary btn-sm w-100"
                target="_blank"
                @click="logClick(site)"
            >
              방문하기 <i class="fas fa-external-link-alt"></i>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const filter = ref('all')

const categories = [
  { key: 'all', label: '전체', class: 'btn-outline-primary' },
  { key: 'it', label: 'IT', class: 'btn-outline-success' },
  { key: 'general', label: '일반', class: 'btn-outline-secondary' },
  { key: 'intern', label: '신입', class: 'btn-outline-warning' },
  { key: 'activity', label: '대외활동', class: 'btn-outline-info' }
]

const jobSites = [
  {
    name: '잡코리아',
    description: '채용 정보 및 연봉 정보 제공',
    link: 'https://www.jobkorea.co.kr',
    category: ['general', 'it']
  },
  {
    name: '사람인',
    description: '국내 대표 취업 사이트',
    link: 'https://www.saramin.co.kr',
    category: ['general', 'it']
  },
  {
    name: '원티드',
    description: '이력서 등록 후 AI 매칭 추천',
    link: 'https://www.wanted.co.kr',
    category: ['it']
  },
  {
    name: '링커리어',
    description: '대외활동과 인턴 공고가 많은 플랫폼',
    link: 'https://www.linkareer.com',
    category: ['general', 'intern']
  },
  {
    name: '점핏',
    description: 'IT 개발자 전문 취업 플랫폼',
    link: 'https://www.jumpit.co.kr',
    category: ['it']
  },
  {
    name: '프로그래머스',
    description: '코딩 테스트 & IT 채용 플랫폼',
    link: 'https://programmers.co.kr',
    category: ['it']
  },
  {
    name: '슈퍼루키',
    description: '인턴·신입·계약직 채용 공고가 많음',
    link: 'https://www.superookie.com',
    category: ['intern']
  },
  {
    name: '위비티',
    description: '스타트업 대외활동 중심 구직 정보',
    link: 'https://www.wevity.com',
    category: ['activity']
  }
  // 필요시 나머지도 추가
]

const filteredSites = computed(() =>
    filter.value === 'all'
        ? jobSites
        : jobSites.filter(site => site.category.includes(filter.value))
)

function logClick(site) {
  const token = localStorage.getItem('token')
  fetch('/site/log', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: token ? `Bearer ${token}` : ''
    },
    body: JSON.stringify({
      siteName: site.name,
      link: site.link
    })
  })
      .then(res => res.json())
      .then(data => console.log('클릭 로그 저장됨:', data))
      .catch(err => console.error('로그 저장 에러:', err))
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 20px auto;
  padding-top: 80px;
}

.card-title {
  font-size: 1.2rem;
}
</style>
