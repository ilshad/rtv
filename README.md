# ...
## Frontend API
#### GET /api/front/conf
#### GET /api/front/s3-put-sign
#### GET /api/front/s3-put-done
#### GET /api/front/video/{id}
#### PUT /api/front/video/{id}/tricks/{start}
#### DELETE /api/front/video/{id}/tricks/{start}
#### POST /api/front/facebook/connect

Отдать токен доступа и фейсбуковский идентификатор пользователя.

Для логина с Facebook используем Facebook JavaScript SDK.
После получения response.status == 'connected', отсылаем полученный
`access_token`, `user_id` на наш сервер.

* Request:
  * Fields (JSON): `access_token`, `user_id`
* Response:
  * Status: 200 OK
  * Fields (JSON): # TODO use info

## Amazon S3
Как включить COR на S3: /doc/S3-CORS.md
