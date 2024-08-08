import cv2

image = cv2.imread('image.jpg')

if image is not None:
    
    kernel_size = (5, 5)  
    blurred_image = cv2.GaussianBlur(image, kernel_size, 0)

    cv2.imshow('Original Image', image)
    cv2.imshow('Blurred Image', blurred_image)
    cv2.waitKey(0)
    cv2.destroyAllWindows()
else:
    print("Image not loaded successfully.")