<script>
export default {
  name: 'FilterChip',
  props: {
    label: String,
    selected: Boolean
  },
  emits: ['toggle'],
  methods: {
    toggle () {
      this.$emit('toggle')
    }
  },
  template: `
    <button
      :class="[
        'px-3 py-1 rounded-full text-sm shadow-sm border',
        selected ? 'bg-indigo-600 text-white border-indigo-600' : 'bg-white hover:bg-gray-50 text-gray-700'
      ]"
      @click="toggle">
      {{ label }}
    </button>
  `
}
</script>
