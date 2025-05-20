import { MotionPlugin } from '@vueuse/motion'

export default {
    install(app) {
        // // 1) 전역 디자인 토큰 CSS 삽입
        const style = document.createElement('style')
        style.innerHTML = `:root{
          /* ◇◇ Color Tokens ◇◇ */
          --c-primary-50:#e8f1ff;
          --c-primary-100:#cfe0ff;
          --c-primary-500:#0d6efd;
          --c-primary-600:#0b5ed7;
          --c-surface:#ffffff;
          --c-surface-dark:#1d1f24;
          --c-text:#212529;
          --c-text-dark:#e5e7eb;
          /* ◇◇ Radius & Elevation */
          --radius-lg:1rem;
          --elev-1:0 1px 3px rgba(0,0,0,.08);
          --elev-2:0 4px 12px rgba(0,0,0,.12);
        }
        @media (prefers-color-scheme:dark){
          :root{
            --c-surface:var(--c-surface-dark);
            --c-text:var(--c-text-dark);
           
          }
           .feed-card .text-muted {
    color: rgba(255, 255, 255, 0.85);
  }

  .feed-card .meta-stats {
    color: rgba(255, 255, 255, 0.8);
  }
          body{background:var(--c-surface);color:var(--c-text);}
        }`;
        document.head.appendChild(style)

        // 2) 마이크로 인터랙션 directive (hover lift)
        app.directive('lift', {
            mounted(el){
                el.style.transition = 'transform .2s ease, box-shadow .2s ease';
                el.addEventListener('pointerenter',()=>{
                    el.style.transform = 'translateY(-4px)';
                    el.style.boxShadow = 'var(--elev-2)';
                })
                el.addEventListener('pointerleave',()=>{
                    el.style.transform = '';
                    el.style.boxShadow = 'var(--elev-1)';
                })
            }
        })

        // 3) vueuse/motion 등록
        app.use(MotionPlugin)
    }
}
