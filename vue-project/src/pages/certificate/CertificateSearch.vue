<template>
  <div class="search-container">
    <h1 class="mb-4 font-weight-bold">자격증 검색</h1>
    <form class="search-box" @submit.prevent="submitSearch">
      <i class="fas fa-search me-2"></i>
      <input type="text" v-model="query" placeholder="검색어를 입력하세요" @input="debouncedFetchSuggestions" />
      <button type="submit"><i class="fas fa-arrow-right"></i></button>
    </form>

    <div v-if="suggestions.length" class="suggestion-box">
      <ul>
        <li v-for="item in suggestions" :key="item.jmfldnm" @click="selectSuggestion(item.jmfldnm)">
          {{ item.jmfldnm }}
        </li>
      </ul>
    </div>


    <div class="graph-layout">
      <aside class="popular-certs-fixed">
        <h6 class="fw-bold mb-3">🔥 인기 자격증</h6>
        <ul class="list-unstyled">
          <li
              v-for="(cert, i) in topCerts"
              :key="cert"
              class="mb-2"
              @click="selectSuggestion(cert)"
          >
            <span class="fw-bold">{{ i + 1 }}. {{ cert }}</span>
          </li>
        </ul>
      </aside>

      <section v-if="result" class="results-container">
        <h5 class="result-title">
          <i class="fas fa-search"></i> 검색 결과
        </h5>

        <div class="result-card">
          <span class="badge badge-primary">{{ result.qualgbnm }}</span>
        </div>

        <div class="graph-main">
          <div class="graph-navigation">
            <button @click="changeGraph('yearlyApplicants')">연도별 지원자 수</button>
            <button @click="changeGraph('passRate')">합격률 분석</button>
            <button @click="changeGraph('examAttempts')">공부 기간</button>
          </div>

          <div class="graph-frame-wrapper">
            <iframe :src="kibanaUrl" class="responsive-iframe" frameborder="0"></iframe>
          </div>
        </div>
      </section>
    </div>
  </div>

</template>

<script setup>
import {onMounted, ref, watch} from 'vue'
import debounce from 'lodash.debounce'
import rison from 'rison'

const query = ref('')
const suggestions = ref([])
const result = ref(null)
const kibanaUrl = ref('')
const topCerts = ref([])
const graphType = ref('yearlyApplicants')

const token = localStorage.getItem('token')
if (!token) {
  alert('로그인이 필요합니다.')
  window.location.href = '/login'
}
onMounted(() => {
  alert('아직 준비중인 기능입니다!')
  window.location.href ="/"
})
fetch('/api/certificate/top5', {
  headers: { Authorization: `Bearer ${token}` }
})
    .then(res => res.json())
    .then(data => {
      topCerts.value = data.certificateTop5 || []
    })

const fetchSuggestions = async () => {
  if (query.value.length < 2) return
  const res = await fetch(`/api/search/certificate?text=${encodeURIComponent(query.value)}`, {
    headers: { Authorization: `Bearer ${token}` }
  })
  const data = await res.json()
  suggestions.value = data.certificate || []
}

const debouncedFetchSuggestions = debounce(fetchSuggestions, 300)

function selectSuggestion(name) {
  query.value = name
  suggestions.value = []
  submitSearch()
}

async function submitSearch() {
  const res = await fetch(`/api/search/certificate?text=${encodeURIComponent(query.value)}`, {
    headers: { Authorization: `Bearer ${token}` }
  })
  const data = await res.json()
  if (data.certificate && data.certificate.length > 0) {
    result.value = data.certificate[0]
    changeGraph(graphType.value)
  }
}

function changeGraph(type) {
  if (!query.value) {
    alert('먼저 검색어를 입력하세요.')
    return
  }
  graphType.value = type
  const certName = query.value
  const baseIndex = {
    yearlyApplicants: '1e886add-517b-41af-8b15-5a766175b6b8',
    passRate: 'fe8a958c-637d-4196-9737-9d3885e493fc',
    examAttempts: '3f6a5c0d-c3eb-4a51-86ad-edffc8f32b7f'
  }

  const filters = [
    {
      "$state": { store: "appState" },
      meta: {
        alias: null,
        disabled: false,
        index: baseIndex[type],
        key: type === 'examAttempts' ? 'jmNm.keyword' : 'jmFldNm.keyword',
        negate: false,
        type: 'phrase',
        value: certName
      },
      query: {
        match_phrase: {
          [type === 'examAttempts' ? 'jmNm.keyword' : 'jmFldNm.keyword']: certName
        }
      }
    }
  ]

  const aggs = {
    yearlyApplicants: [
      { id: '1', type: 'sum', params: { field: 'recptCnt' }, schema: 'metric' },
      { id: '2', type: 'terms', params: { field: 'statisYy', order: 'desc', size: 10 }, schema: 'segment' }
    ],
    passRate: [
      { id: '1', type: 'avg', params: { field: 'silPassRate' }, schema: 'metric' },
      { id: '3', type: 'avg', params: { field: 'pilPassRate' }, schema: 'metric' },
      { id: '2', type: 'terms', params: { field: 'statisYy', order: 'desc', size: 5 }, schema: 'segment' }
    ],
    examAttempts: [
      { id: '1', type: 'sum', params: { field: 'passManCnt' }, schema: 'metric' },
      { id: '2', type: 'terms', params: { field: 'trendDtlDivNm', size: 5 }, schema: 'segment' }
    ]
  }

  const filterObj = {
    filters,
    vis: {
      aggs: aggs[type],
      params: { addTooltip: true, addLegend: true },
      type: 'line'
    }
  }

  kibanaUrl.value = `https://kibana.montkim.com/app/visualize#/create?embed=true&type=line&indexPattern=${baseIndex[type]}&_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-1y,to:now))&_a=${encodeURIComponent(rison.encode(filterObj))}`
}
</script>

<style scoped>
.search-container {
  margin-top: 100px;
  text-align: center;
}

.graph-layout {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-top: 30px;
}

.popular-certs-fixed {
  position: absolute;
  left: 0;
  top: 300px;
  width: 200px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0px 4px 10px rgba(0,0,0,0.1);
}
.results-container {
  flex: 1 1 700px;
  max-width: 900px;
  margin: 0 auto;
  text-align: center;
}
.result-title {
  margin-bottom: 12px;
}
.graph-main {
  text-align: center;
}

.graph-navigation {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 20px;
}
.graph-navigation button:hover {
  background: #007bff;
  color: white;
}
.graph-frame-wrapper {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  aspect-ratio: 16 / 9;
  position: relative;
}

.responsive-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
.popular-certs-banner h6 {
  font-size: 16px;
  margin-bottom: 15px;
}

.popular-certs-banner li {
  cursor: pointer;
  padding: 6px 10px;
  font-size: 14px;
  border-radius: 6px;
  transition: background 0.2s;
}

.popular-certs-banner li:hover {
  background-color: #f1f3f5;
}

.popular-certs-banner li:hover {
  background-color: #f8f9fa;
}
.graph-navigation button {
  padding: 10px 20px;
  border-radius: 25px;
  background: #f1f3f5;
  border: 1px solid #ccc;
  font-size: 15px;
  transition: 0.2s;
}
.graph-navigation button:hover {
  background: #007bff;
  color: white;
}

.search-box {
  background: white;
  padding: 15px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  width: 90%;
  max-width: 600px;
  margin: auto;
}

.graph-navigation button:hover {
  background: #007bff;
  color: white;
  border-color: #007bff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.search-box input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 18px;
}
.search-box button {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 50px;
  cursor: pointer;
}
.suggestion-box {
  background: white;
  border: 1px solid #ddd;
  border-radius: 12px;
  max-width: 600px;
  margin: auto;
  margin-top: 5px;
  z-index: 1000;
}
.suggestion-box ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.suggestion-box li {
  padding: 10px;
  cursor: pointer;
}

</style>
