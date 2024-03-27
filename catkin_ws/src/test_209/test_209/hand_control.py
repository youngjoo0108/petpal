import rclpy, time
from rclpy.node import Node
import os, time
from ssafy_msgs.msg import TurtlebotStatus,HandControl

# Hand Control 노드는 시뮬레이터로부터 데이터를 수신해서 확인(출력)하고, 메세지를 송신해서 Hand Control기능을 사용해 보는 노드입니다. 
# 메시지를 받아서 Hand Control 기능을 사용할 수 있는 상태인지 확인하고, 제어 메시지를 보내 제어가 잘 되는지 확인해보세요. 
# 수신 데이터 : 터틀봇 상태 (/turtlebot_status)
# 송신 데이터 : Hand Control 제어 (/hand_control)


# 노드 로직 순서
# 1. publisher, subscriber 만들기
# 2. 사용자 메뉴 구성
# 3. Hand Control Status 출력
# 4. Hand Control - Preview
# 5. Hand Control - Pick up
# 6. Hand Control - Put down


class Handcontrol(Node):

    def __init__(self):
        super().__init__('hand_control')
                
        ## 로직 1. publisher, subscriber 만들기
        self.hand_control_pub = self.create_publisher(HandControl, '/hand_control', 10)                
        # self.hand_control_sub = self.create_subscription(HandControl, '/hand_control', self. , 10)                
        self.turtlebot_status = self.create_subscription(TurtlebotStatus,'/turtlebot_status',self.turtlebot_status_cb,10)

        # self.timer = self.create_timer(1, self.timer_callback)
        
        ## 제어 메시지 변수 생성 
        self.hand_control_msg=HandControl()        


        self.turtlebot_status_msg = TurtlebotStatus()
        self.is_turtlebot_status = False
        

    # def timer_callback(self):
        
    #     print('Select Menu [0: status_check, 1: preview, 2:pick_up, 3:put_down')
    #     menu=input(">>")
    #     while True:
    #         # 로직 2. 사용자 메뉴 구성
    #         print(menu)
    #         if menu=='0' :
    #             self.hand_control_status()
    #             break
    #         if menu=='1' :
    #             self.hand_control_preview()
    #             break               
    #         if menu=='2' :
    #             self.hand_control_pick_up()
    #             if self.turtlebot_status_msg.can_put == False:
    #                 break
    #             menu = '2'
    #         if menu=='3' :
    #             self.hand_control_put_down()
    #             if self.turtlebot_status_msg.can_use_hand == False:
    #                 break
    #             menu == '3'

    def hand_control_status(self):
        # 로직 3. Hand Control Status 출력
        if self.is_turtlebot_status:
            print(f"Turtlebot Status - can_lift: {self.turtlebot_status_msg.can_lift}, put: {self.turtlebot_status_msg.can_put}, is_lifted: {self.turtlebot_status_msg.can_use_hand}")
        else:
            print("Turtlebot status is not yet received.")

    def hand_control_preview(self):
        # 로직 4. Hand Control - Preview
        self.hand_control_msg.control_mode = 1  # 가정된 control_mode 값
        self.hand_control_pub.publish(self.hand_control_msg)
        print("Hand control preview mode activated.")

    def hand_control_pick_up(self):
        # 로직 5. Hand Control - Pick up
        self.hand_control_msg.control_mode = 2  # 가정된 control_mode 값
        self.hand_control_msg.put_distance = 1.0
        self.hand_control_msg.put_height = 0.0
        # while self.turtlebot_status_msg.can_put == False:
        self.hand_control_pub.publish(self.hand_control_msg)
        # print('logic1')
            
        print("Hand control pick-up mode activated.")
        

    def hand_control_put_down(self):
        # 로직 6. Hand Control - Put down
        self.hand_control_msg.control_mode = 3  # 가정된 control_mode 값
        self.hand_control_msg.put_distance = 1.0
        self.hand_control_msg.put_height = 0.5
        # while self.turtlebot_status_msg.can_use_hand == True:
        
        self.hand_control_pub.publish(self.hand_control_msg)
        print("Hand control put-down mode activated.")
        

    def turtlebot_status_cb(self,msg):
        self.is_turtlebot_status=True
        self.turtlebot_status_msg=msg

        print('Select Menu [0: status_check, 1: preview, 2:pick_up, 3:put_down')
        menu=input(">>")
        # while True:
            # 로직 2. 사용자 메뉴 구성
        print(menu)
        if menu=='0' :
            self.hand_control_status()
            
        if menu=='1' :
            self.hand_control_preview()
                           
        if menu=='2' :
            while self.turtlebot_status_msg.can_lift == True:
                print(self.turtlebot_status_msg.can_lift)
                if self.turtlebot_status_msg.can_lift == False:
                    break
                self.hand_control_pick_up()

                
        if menu=='3' :
            
            while self.turtlebot_status_msg.can_use_hand == True:
                print(self.turtlebot_status_msg.can_use_hand)
                if self.turtlebot_status_msg.can_use_hand == False:
                    break
                self.hand_control_put_down()
        

def main(args=None):
    rclpy.init(args=args)
    sub1_hand_control = Handcontrol()    
    rclpy.spin(sub1_hand_control)
    sub1_hand_control.destroy_node()
    rclpy.shutdown()

if __name__ == '__main__':
    main()