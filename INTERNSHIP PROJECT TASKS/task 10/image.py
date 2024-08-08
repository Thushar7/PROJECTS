import cv2
import subprocess

def run_another_script():
    script_path = 'task10\calculate.py'  # Change this to the path of your script
    subprocess.call(['python', script_path])
cam=cv2.VideoCapture(0)

count=0

while True:
    ret,img=cam.read()
    cv2.imshow("Test",img)


    if not ret:
        break

    k=cv2.waitKey(1)

    if k%256==27:

        print("Close")
        break
    elif k%256==32:

        print("Image"+str(count)+"saved")
        file='C:/Users/hp/Desktop/project-2/task10-/captured_image_saved/img'+str(count)+'.jpg'
        cv2.imwrite(file,img)
        count +=1
        print("press esc to find the focal length of the captured image")
cam.release
run_another_script()