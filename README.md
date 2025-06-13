Microservice-EVO
---
> Project mô phỏng lại cơ chế , cách hoạt động cơ bản của hệ thống microservice 
---
1. IAM SERVICE - Service định danh , có tích hợp keycloak , có cơ chế bật tắt keycloak xác thực , chuyên để nhận nhiệm vụ xác thực User
2. STORAGE SERVICE - Service nghiệp vụ, dùng để thực hiện 1 số chức năng liên quan đến ảnh, fileEntity của người dùng 
---
1 số điểm lưu ý :
  + Triển khai theo cơ chế ddd, có sử dụng azura keyvault để bảo mật key secret
  + Sử dụng SpringBoot, Spring Eureka, Spring Security
  + Sử dụng PostgresSQL, Redis , Keycloak
  + Triển khai cơ chế các Service gọi nhau bằng Restemplate/ FeignClient
  + Triển khai API Gateway, Discovery Service
  + Triển khai Common để có thể tích hợp chung và sau này dễ dàng tạo mới và phát triển các Service con khác
    
