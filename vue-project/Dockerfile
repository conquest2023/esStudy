FROM node:18-alpine AS build

WORKDIR /app
# 전체 파일을 복사합니다.
COPY . .

WORKDIR /app/vue-project

RUN npm install && npm run build

RUN ls -la /app/vue-project/dist

FROM nginx:alpine
# 빌드 결과물이 /app/vue-project/dist에 생성되므로 해당 경로에서 복사합니다.
COPY --from=build /app/vue-project/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]