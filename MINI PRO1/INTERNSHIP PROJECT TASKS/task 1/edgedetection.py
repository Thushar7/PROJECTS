import cv2
import numpy as np

image = cv2.imread('image.jpg', cv2.IMREAD_GRAYSCALE)

if image is not None:
    blurred_image = cv2.GaussianBlur(image, (5, 5), 0)

    edges = cv2.Canny(blurred_image, 50, 150)  
    cv2.imshow('Original Image', image)
    cv2.imshow('Blurred Image', blurred_image)
    cv2.imshow('Edges', edges)
    cv2.waitKey(0)
    cv2.destroyAllWindows()
else:
    print("Image not loaded successfully.")
