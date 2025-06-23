<script>
export default {
  name: 'QuestionModal',
  props: {
    question: Object
  },
  emits: ['close'],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="$emit('close')">
      <div class="bg-white rounded-2xl shadow-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto p-8">
        <h2 class="text-xl font-bold mb-4">{{ question.title }}</h2>
        <div class="prose" v-html="question.body"></div>
        <hr class="my-6">
        <h3 class="font-semibold mb-2">모범 답안 / 해설</h3>
        <div class="prose" v-html="question.answerKey"></div>
        <button
          class="mt-8 px-6 py-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 mx-auto block"
          @click="$emit('close')">닫기</button>
      </div>
    </div>`
}
</script>
