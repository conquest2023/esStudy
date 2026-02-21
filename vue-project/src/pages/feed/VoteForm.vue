<template>
  <div class="container-xl py-5 write-container">
    <div class="mx-auto modern-write-card shadow-sm">

      <header class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom-soft">
        <h3 class="m-0 fw-bold text-dark"><i class="fas fa-poll-h text-primary me-2"></i>투표 작성하기</h3>
        <button class="btn-cancel-soft" type="button" @click="router.back()">취소</button>
      </header>

      <form @submit.prevent="submitForm">
        <div class="mb-4">
          <input
              v-model="form.title"
              type="text"
              class="title-input mb-3"
              placeholder="투표 제목을 입력하세요"
              required
          />
          <textarea
              v-model="form.description"
              class="modern-input"
              rows="3"
              placeholder="투표에 대한 상세한 설명을 적어주세요. (선택)"
          ></textarea>
        </div>

        <div class="poll-options-section mb-5">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <label class="fw-bold text-dark fs-5 m-0">투표 항목</label>
            <span class="badge-pill bg-secondary-soft">{{ form.options.length }} / 5</span>
          </div>

          <div class="options-list">
            <TransitionGroup name="list" tag="div">
              <div
                  v-for="(option, index) in form.options"
                  :key="'opt-' + index"
                  class="option-row"
              >
                <div class="option-icon"><i class="fas fa-grip-vertical text-muted small"></i></div>
                <input
                    v-model="option.content"
                    type="text"
                    class="modern-input flex-grow-1"
                    :placeholder="`항목 ${index + 1} 입력`"
                    required
                />
                <button
                    type="button"
                    class="icon-btn-soft text-danger"
                    @click="removeOption(index)"
                    title="항목 삭제"
                >
                  <i class="fas fa-minus-circle"></i>
                </button>
              </div>
            </TransitionGroup>
          </div>

          <button
              type="button"
              class="btn-add-option w-100 mt-2"
              :class="{ 'disabled': form.options.length >= 5 }"
              @click="addOption"
          >
            <i class="fas fa-plus me-2"></i>새로운 항목 추가하기
          </button>
        </div>

        <div class="settings-panel bg-light-soft p-4 rounded-4 mb-5">
          <h6 class="fw-bold text-dark mb-3"><i class="fas fa-cog text-secondary me-2"></i>투표 설정</h6>

          <div class="row gy-3">
            <div class="col-12 col-md-6">
              <div class="d-flex align-items-center justify-content-between bg-white p-3 rounded-3 border-soft">
                <span class="fw-semibold text-secondary">익명 투표 설정</span>
                <div class="form-check form-switch m-0 custom-switch">
                  <input id="anonymous" v-model="form.anonymous" type="checkbox" class="form-check-input" />
                </div>
              </div>
            </div>

            <div class="col-12 col-md-6">
              <div class="d-flex align-items-center justify-content-between bg-white p-3 rounded-3 border-soft">
                <span class="fw-semibold text-secondary">복수 선택 허용</span>
                <div class="form-check form-switch m-0 custom-switch">
                  <input id="multiSelect" v-model="form.multiSelect" type="checkbox" class="form-check-input" />
                </div>
              </div>
            </div>

            <Transition name="fade-slide">
              <div class="col-12" v-if="form.multiSelect">
                <div class="d-flex align-items-center bg-white p-3 rounded-3 border-soft">
                  <span class="fw-semibold text-secondary me-3 text-nowrap">최대 선택 개수</span>
                  <input
                      v-model.number="form.maxSelectCnt"
                      type="number"
                      min="1"
                      :max="form.options.length"
                      class="modern-input py-1 text-center"
                      style="width: 100px;"
                      placeholder="ex) 2"
                      required
                  />
                  <span class="ms-2 small text-muted">개</span>
                </div>
              </div>
            </Transition>
          </div>
        </div>

        <button type="submit" class="btn-submit-glow w-100">
          <i class="fas fa-check-circle me-2"></i> 투표 등록하기
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  category: '',
  title: '',
  description: '',
  multiSelect: false,
  maxSelectCnt: null,
  anonymous: false,
  options: [
    { content: '' } // 처음에 1개
  ]
})

function addOption() {
  if (form.value.options.length < 5) {
    form.value.options.push({ content: '' })
  } else {
    alert('투표 항목은 최대 5개까지 추가할 수 있습니다.')
  }
}

function removeOption(index) {
  if (form.value.options.length > 1) {
    form.value.options.splice(index, 1)
  } else {
    alert('최소한 하나의 투표 항목은 있어야 합니다.')
  }
}

async function submitForm() {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('로그인이 필요합니다.')
    return
  }

  const payload = {
    title: form.value.title,
    description: form.value.description,
    multiSelect: form.value.multiSelect,
    maxSelectCnt: form.value.multiSelect ? form.value.maxSelectCnt : null,
    anonymous: form.value.anonymous,
    options: form.value.options.map((opt, index) => ({
      content: opt.content,
      sortOrder: index
    })),
    closesAt: null
  }

  try {
    const res = await fetch('/api/poll', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    })
    const result = await res.json()

    if (res.ok) {
      alert('투표가 성공적으로 등록되었습니다!')
      router.push('/')
    } else {
      alert(result.error || '투표 등록에 실패했습니다.')
    }
  } catch (error) {
    console.error('투표 저장 오류:', error)
    alert('서버 오류가 발생했습니다.')
  }
}
</script>

<style scoped>
/* ===============================
   전체 폼 래퍼 & 모던 카드
================================= */
.write-container {
  max-width: 800px;
}

.modern-write-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 40px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.03);
}

.border-bottom-soft {
  border-bottom: 1px solid #f1f5f9;
}

.bg-light-soft {
  background: #f8fafc;
}

.border-soft {
  border: 1px solid #f1f5f9;
}

/* 취소 버튼 */
.btn-cancel-soft {
  background: #f1f5f9;
  color: #64748b;
  border: none;
  padding: 8px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.btn-cancel-soft:hover {
  background: #e2e8f0;
  color: #334155;
}

/* ===============================
   노션 스타일 입력 필드
================================= */
.title-input {
  width: 100%;
  border: none;
  border-bottom: 2px solid #f1f5f9;
  font-size: 2.2rem;
  font-weight: 800;
  color: #0f172a;
  padding: 10px 0;
  background: transparent;
  outline: none;
  transition: border-color 0.2s;
  letter-spacing: -0.02em;
}

.title-input:focus {
  border-bottom-color: #2563eb;
}

.title-input::placeholder {
  color: #cbd5e1;
  font-weight: 700;
}

.modern-input {
  width: 100%;
  background: #f8fafc;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 0.95rem;
  color: #0f172a;
  resize: none;
  transition: all 0.2s ease;
}

.modern-input:focus {
  outline: none;
  background: #ffffff;
  border-color: #bfdbfe;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.modern-input::placeholder {
  color: #94a3b8;
}

/* ===============================
   투표 항목 리스트
================================= */
.bg-secondary-soft {
  background: #e2e8f0;
  color: #475569;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.85rem;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-row {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #ffffff;
}

.option-icon {
  width: 24px;
  text-align: center;
  color: #cbd5e1;
  cursor: grab;
}

.icon-btn-soft {
  background: transparent;
  border: none;
  font-size: 1.25rem;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  color: #cbd5e1;
}

.icon-btn-soft:hover {
  background: #fee2e2;
  color: #ef4444 !important;
}

/* 항목 추가 점선 버튼 */
.btn-add-option {
  background: #f8fafc;
  border: 2px dashed #cbd5e1;
  color: #64748b;
  padding: 14px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-option:hover:not(.disabled) {
  background: #eff6ff;
  border-color: #93c5fd;
  color: #2563eb;
}

.btn-add-option.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ===============================
   iOS 스타일 스위치 & 설정
================================= */
.custom-switch .form-check-input {
  width: 2.5em;
  height: 1.25em;
  cursor: pointer;
  border-color: #cbd5e1;
}

.custom-switch .form-check-input:checked {
  background-color: #2563eb;
  border-color: #2563eb;
}

/* ===============================
   제출 버튼 (Glow)
================================= */
.btn-submit-glow {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
  border: none;
  padding: 14px;
  border-radius: 14px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.3);
  transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.btn-submit-glow:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.4);
}

/* ===============================
   Vue Transition 애니메이션
================================= */
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from, .fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 리스트 추가/삭제 애니메이션 */
.list-enter-active, .list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateY(15px);
}

.list-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* 반응형 */
@media (max-width: 576px) {
  .modern-write-card {
    padding: 24px;
    border-radius: 16px;
  }

  .title-input {
    font-size: 1.6rem;
  }
}
</style>