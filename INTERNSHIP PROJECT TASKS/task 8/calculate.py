import cv2
import numpy as np
import os
import math
image_path = 'task2\line.jpeg'
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

    # Display the image with the thinnest point
    output_file = os.path.join('task8', 'output_image.jpg')
    cv2.imwrite(output_file, img_thinnest)

    return thinnest_point

    print(f"Output image saved as {output_file}")

    
try:
    # Find the thinnest point on the line
    thinnest_point = find_thinnest_line(image_path)
    print("Thinnest Point Coordinates (x, y):", (thinnest_point[0, 0] + thinnest_point[0, 2]) // 2, (thinnest_point[0, 1] + thinnest_point[0, 3]) // 2)
except FileNotFoundError as e:
    print(f"Error: {e}")
except ValueError as e:
    print(f"Error: {e}")

# Load the image
image = cv2.imread('task8\output_image.jpg')
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

# Apply edge detection (if needed)
edges = cv2.Canny(gray, 50, 150, apertureSize=3)

# Detect lines using Hough Line Transform
lines = cv2.HoughLines(edges, 1, np.pi / 180, 100)

# Draw detected lines on a copy of the original image
line_image = np.copy(image)
for line in lines:
    rho, theta = line[0]
    a = np.cos(theta)
    b = np.sin(theta)
    x0 = a * rho
    y0 = b * rho
    x1 = int(x0 + 1000 * (-b))
    y1 = int(y0 + 1000 * (a))
    x2 = int(x0 - 1000 * (-b))
    y2 = int(y0 - 1000 * (a))
    cv2.line(line_image, (x1, y1), (x2, y2), (0, 0, 255), 2)

# Straighten the detected line
angle = theta * 180 / np.pi
if angle < 45:
    angle += 90
else:
    angle -= 90

h, w = image.shape[:2]
center = (w // 2, h // 2)
rotation_matrix = cv2.getRotationMatrix2D(center, angle, 1.0)
rotated_image = cv2.warpAffine(image, rotation_matrix, (w, h), flags=cv2.INTER_LINEAR)

# Save the rotated image
output_file = os.path.join('task8', 'output_final.jpg')
cv2.imwrite(output_file, rotated_image)

print(f"Output image saved as {output_file}")
ruler_image=cv2.imread(r'task2\ruler.jpg')
rotated_image=cv2.imread(r'task2\output_final.jpg')

thinnest_point_x = (thinnest_point[0, 0] + thinnest_point[0, 2]) // 2
ruler_width = ruler_image.shape[1]
vertical_line_x = (thinnest_point_x / rotated_image.shape[1]) * ruler_width

# Draw a vertical line on the ruler image
vertical_line_color = (0, 255, 0)  # Green color
line_thickness = 2
cv2.line(ruler_image, (int(vertical_line_x), 0), (int(vertical_line_x), ruler_image.shape[0]), vertical_line_color, line_thickness)


resized_ruler_image = cv2.resize(ruler_image, (rotated_image.shape[1], ruler_image.shape[0]))


concatenated_image = np.vstack((resized_ruler_image,rotated_image))

output_file = os.path.join('task8', 'output_final_image.jpg')
cv2.imwrite(output_file, concatenated_image)
ruler_range_min = 9 # Minimum value on the ruler scale
ruler_range_max = 0  # Maximum value on the ruler scale

# Determine the position of the thinnest point on the scale
thinnest_point_normalized = vertical_line_x / ruler_width
thinnest_point_value = thinnest_point_normalized * (ruler_range_max - ruler_range_min) + ruler_range_min

print("Value corresponding to the thinnest point:",math.ceil( thinnest_point_value))