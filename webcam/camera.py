import cv2
import datetime as dt
import time
import pytz
import os

#VideoCapture 객체 생성
vid_capture = cv2.VideoCapture(0)

#가로세로 원본 크기 그대로 지정
width=int(vid_capture.get(3))
height=int(vid_capture.get(4))

#코덱 적용(DIVX, XVID, MJPG, X264, WMV1, WMV2)
vid_cod = cv2.VideoWriter_fourcc(*'XVID')


while True:
    #비디오 저장을 위한 폴더 생성
    #해당 날짜 디렉터리 없으면 생성, 있으면 넘어가기
    KST = pytz.timezone("Asia/Seoul")
    now = dt.datetime.now(KST)
    ymd = now.strftime("%y_%m_%d")
    HMS = now.strftime("%H_%M_%S")

    #print(now.strftime("%y-%m-%d-%H-%M-%S")) 년 월 일 시 분 초
    directory = os.listdir("videos/")
    if ymd not in directory:
        os.mkdir("videos/" + ymd)
        print(ymd)

    #비디오 저장을 위한 객체 생성
    output = cv2.VideoWriter("videos/{}/{}.avi".format(ymd, HMS), vid_cod, 30, (width,height))
    start_time = time.time()

    while True:
        #비디오 한 프레임씩 읽어옴
        #성공한경우 ret == True / 읽어온 프레임은 frame
        ret, frame = vid_capture.read()

        #화면출력
        #cv2.imshow("Mycam", frame)

        #프레임 저장
        output.write(frame)


        video_time = time.time()
        print(video_time-start_time)
        if video_time-start_time > 60:
            break

    #vid_capture.release()
    output.release()
    #cv2.destroyAllWindows







