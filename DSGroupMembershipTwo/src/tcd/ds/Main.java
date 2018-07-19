package tcd.ds;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
	private static Logger LOG;

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		LOG = Logger.getLogger(Main.class.getName());
		try {

			System.out.println("Enter a unique User ID!!");
			int randomVal = (int) (Math.random() * 1000);
			String memberId = scanner.nextLine() + randomVal;
			LOG.info("Your unique MemberID is :: " + memberId);
			DSServer server = new DSServer(memberId);

			while (true) {
				System.out.println(
						"*************************************************************************************\n"
								+ "Commands: 1>Join Group | 2>Share View | 3>Leave Group | 4>My View");

				int cmd = scanner.nextInt();

				if (cmd == 1) {
					System.out.println("Enter GroupID to join");
					cmd = scanner.nextInt();
					server.joinGroup(cmd);
					cmd = 0;
				}

				if (cmd == 2) {
					System.out.println("Enter GroupID to share view with ");
					cmd = scanner.nextInt();
					server.sendView(cmd);
					cmd = 0;
				}

				if (cmd == 3) {
					System.out.println("Enter GroupID to leave");
					cmd = scanner.nextInt();
					server.leaveGroup(cmd);
					cmd = 0;
				}

				if (cmd == 4) {
					System.out.println("Enter GroupID to check members!");
					cmd = scanner.nextInt();
					server.showMyView(cmd);
					cmd = 0;
				}

				cmd = 0;
			}

		} catch (Exception e) {
			
		} finally {
			scanner.close();
		}
		

	}


}
