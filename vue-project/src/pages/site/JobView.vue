<template>

  <div class="container my-4">

    <section class="section">
      <div class="section-title">🔥 점핏 채용 공고</div>
      <div class="job-list">
        <template v-if="jumpitJobs.length > 0">
          <a
              v-for="job in jumpitJobs"
              :key="job.url"
              :href="job.url"
              target="_blank"
              class="card"
          >
            <h3 class="job-title">{{ job.title }}</h3>
            <p class="company-name">{{ job.companyName }}</p>
            <p class="location">📍 {{ job.location }}</p>
            <div class="tech-stack">
              <span v-for="stack in job.techStacks" :key="stack" class="tech-tag">{{ stack }}</span>
            </div>
          </a>
        </template>
        <p v-else class="no-data">채용 공고가 없습니다.</p>
      </div>
    </section>

    <!-- 프로그래머스 섹션 -->
    <section class="section">
      <div class="section-title">🚀 프로그래머스 채용 공고</div>
      <div class="job-list">
        <template v-if="programmersJobs.length > 0">
          <a
              v-for="job in programmersJobs"
              :key="job.url"
              :href="job.url"
              target="_blank"
              class="card"
          >
            <h3 class="job-title">{{ job.title }}</h3>
            <p class="company-name">{{ job.companyName }}</p>
            <p class="location">📍 {{ job.location }}</p>
            <div class="tech-stack">
              <span v-for="stack in job.techStacks" :key="stack" class="tech-tag">{{ stack }}</span>
            </div>
          </a>
        </template>
        <p v-else class="no-data">채용 공고가 없습니다.</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const jumpitJobs = ref([])
const programmersJobs = ref([])

onMounted(async () => {
  try {
    const [jumpitRes, programmersRes] = await Promise.all([
      fetch('/api/jumpit').then(res => res.json()),
      fetch('/api/programmers').then(res => res.json())
    ])

    jumpitJobs.value = Array.isArray(jumpitRes)
        ? jumpitRes.map(job => ({
          title: job.title,
          companyName: job.companyName,
          location: job.locations?.join(', ') || '위치 정보 없음',
          techStacks: job.techStacks || [],
          url: `https://www.jumpit.co.kr/position/${job.id}`
        }))
        : []

    programmersJobs.value = Array.isArray(programmersRes)
        ? programmersRes.map(job => ({
          title: job.title,
          companyName: job.companyName,
          location: job.address || '위치 정보 없음',
          techStacks: job.technicalTags || [],
          url: `https://career.programmers.co.kr/job_positions/${job.id}`
        }))
        : []
  } catch (err) {
    console.error('채용 데이터 로딩 실패:', err)
  }
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 20px auto;
}
.section {
  margin-bottom: 40px;
}
.section-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
  padding: 10px;
  background: #007bff;
  color: white;
  border-radius: 5px;
}
.job-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}
.container {
  max-width: 1200px;
  margin: 20px auto;
  padding-top: 80px;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  text-decoration: none;
  color: inherit;
  display: block;
}
.card:hover {
  transform: translateY(-5px);
}
.card h3 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #333;
}
.company-name {
  font-size: 16px;
  color: #666;
}
.location {
  font-size: 14px;
  color: #888;
}
.tech-stack {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}
.tech-tag {
  background: #007bff;
  color: white;
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 12px;
}
.no-data {
  font-size: 16px;
  color: #999;
  text-align: center;
  padding: 20px;
}
</style>
