#It is for Send data to Server
import rclpy
from rclpy.node import Node

from std_msgs.msg import String


class StreamingPublisher(Node):

    def __init__(self):
        super().__init__('minimal_subscriber')
        self.subscription = self.create_subscription(
            String,
            'topic',
            self.listener_callback,
            10)
        self.subscription  # prevent unused variable warning

    def listener_callback(self, msg):
        self.get_logger().info('I heard: "%s"' % msg.data)


def main(args=None):
    rclpy.init(args=args)

    #data publisher createde
    streaming_publisher = StreamingPublisher()

    #execute class objects.
    rclpy.spin(streaming_publisher)

    streaming_publisher.destroy_node()
    rclpy.shutdown()

if __name__ == '__main__':
    main()