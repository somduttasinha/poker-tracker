FROM node:24.3-alpine3.22 AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build

FROM nginx:1.29.0-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
