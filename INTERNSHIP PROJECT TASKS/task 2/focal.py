import cv2
import numpy as np
import os

def find_thinnest_line(image_path):
    # Check if the image file exists
    if not os.path.isfile(image_path):
        raise FileNotFoundError(f"Image file not found: {image_path}")

    img = cv2.imread(image_path)
    
    if img is None:
        raise ValueError(f"Error loading image: {image_path}")

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # Apply Canny edge detection
    edges = cv2.Canny(gray, 50, 150, apertureSize=3)

    # Detect lines using HoughLinesP
    lines = cv2.HoughLinesP(edges, rho=1, theta=np.pi/180, threshold=100, minLineLength=100, maxLineGap=10)

    if lines is None or len(lines) == 0:
        raise ValueError("No lines detected in the image.")

    # Calculate the distance of each pixel on the line from the center of the image
    image_center = (img.shape[1] // 2, img.shape[0] // 2)

    def distance_from_center(x, y):
        return np.sqrt((x - image_center[0])*2 + (y - image_center[1])*2)

    # Find the thinnest point on the line based on the distance from the center
    thinnest_point = min(lines, key=lambda line: distance_from_center((line[0, 0] + line[0, 2]) // 2, (line[0, 1] + line[0, 3]) // 2))

    # Draw a small circle at the thinnest point on the original image
    img_thinnest = img.copy()
    x, y = (thinnest_point[0, 0] + thinnest_point[0, 2]) // 2, (thinnest_point[0, 1] + thinnest_point[0, 3]) // 2
    cv2.circle(img_thinnest, (x, y), 5, (0, 0, 255), -1)

    output_file = os.path.join('task2', 'output_thinnest_point_image.jpg')
    cv2.imwrite(output_file, img_thinnest)

    return thinnest_point

    print(f"Output image saved as {output_file}")
image_path = 'task2\line.jpeg'

try:
    # Find the thinnest point on the line
    thinnest_point = find_thinnest_line(image_path)
    print("Thinnest Point Coordinates (x, y):", (thinnest_point[0, 0] + thinnest_point[0, 2]) // 2, (thinnest_point[0, 1] + thinnest_point[0, 3]) // 2)
except FileNotFoundError as e:
    print(f"Error: {e}")
except ValueError as e:
    print(f"Error: {e}")

