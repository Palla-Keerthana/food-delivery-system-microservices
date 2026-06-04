@echo off
start "Eureka" cmd /k "cd /d C:\Users\2499380\IdeaProjects\food-delivery-system-microservices\infrastructure\eureka-server\eureka-server && mvnw.cmd spring-boot:run"
timeout /t 20 /nobreak
start "Gateway" cmd /k "cd /d C:\Users\2499380\IdeaProjects\food-delivery-system-microservices\infrastructure\api-gateway\api-gateway && mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak
start "Auth" cmd /k "cd /d C:\Users\2499380\IdeaProjects\food-delivery-system-microservices\services\auth-service && mvnw.cmd spring-boot:run"
timeout /t 10 /nobreak
start "Customer" cmd /k "cd /d C:\Users\2499380\IdeaProjects\food-delivery-system-microservices\services\customer-service && mvnw.cmd spring-boot:run"
timeout /t 10 /nobreak
start "Menu" cmd /k "cd /d C:\Users\2499380\IdeaProjects\food-delivery-system-microservices\services\menu-service && mvnw.cmd spring-boot:run"
timeout /t 10 /nobreak
start "Order" cmd /k "cd /d C:\Users\2499380\IdeaProjects\food-delivery-system-microservices\services\order-service && mvnw.cmd spring-boot:run"
timeout /t 10 /nobreak
start "Payment" cmd /k "cd /d C:\Users\2499380\IdeaProjects\food-delivery-system-microservices\services\payment-service && mvnw.cmd spring-boot:run"
timeout /t 10 /nobreak
start "Delivery" cmd /k "cd /d C:\Users\2499380\IdeaProjects\food-delivery-system-microservices\services\delivery-service\delivery-service && mvnw.cmd spring-boot:run"
echo All services starting!
pause