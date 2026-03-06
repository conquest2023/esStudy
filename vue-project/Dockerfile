# Node 18 대신 20 사용
FROM node:20-alpine AS build

WORKDIR /app
COPY package*.json ./
# 의존성 먼저 설치 (캐시 최적화)
RUN npm install --legacy-peer-deps

COPY . .
# 빌드 실행 (에러 로그 확인을 위해)
RUN npm run build

FROM nginx:alpine
# 빌드 결과물 복사
COPY --from=build /app/dist /usr/share/nginx/html
# nginx 설정 복사 (파일이 실제 루트에 있는지 확인!)
COPY nginx.conf /etc/nginx/conf.d/default.conf 
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]