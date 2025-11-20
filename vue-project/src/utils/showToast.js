
export function showToast(message, postId) {
    const container = document.getElementById('toastContainer')
    if (!container) return

    const toast = document.createElement('div')
    toast.className = 'toast align-items-center bg-dark border-0 shadow mb-2 show'
    toast.setAttribute('role', 'alert')
    toast.setAttribute('aria-live', 'assertive')
    toast.setAttribute('aria-atomic', 'true')
    toast.innerHTML = `
    <div class="d-flex">
      <div class="toast-body fw-bold text-white">
        🔔 <a href="/post/${postId}" class="text-white text-decoration-underline">${message}</a>
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>`
    container.appendChild(toast)
    setTimeout(() => {
        toast.classList.remove('show')
        toast.addEventListener('transitionend', () => toast.remove())
    }, 5000)
}
