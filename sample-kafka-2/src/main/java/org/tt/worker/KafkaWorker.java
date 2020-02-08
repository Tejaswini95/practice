package org.tt.worker;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.tt.worker.IKafkaConstants;
import org.tt.worker.ConsumerCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaWorker {

	public static void main(String[] args) {
		int i = 1;
		while (true) {
			if (i >= 60) {
				break;
			}
			runConsumer();

			i++;
			try {
				Thread.sleep(3 * 1000);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	static void runConsumer() {
		Consumer<Long, String> consumer = ConsumerCreator.createConsumer();
		int noMessageFound = 0;
		while (true) {
			ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			// 1000 is the time in milliseconds consumer will wait if no record is found at
			// broker.
			if (consumerRecords.count() == 0) {
				noMessageFound++;
				if (noMessageFound > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					// If no message found count is reached to threshold exit loop.
					break;
				else
					continue;
			}
			// print each record.
			consumerRecords.forEach(record -> {
				System.out.println("Record Key " + record.key());
				System.out.println("Record value " + record.value());
				System.out.println("Record partition " + record.partition());
				System.out.println("Record offset " + record.offset());
				Runnable worker = new CopyWorker(record.value());
				new Thread(worker).start();
				System.out.print(new Date(System.currentTimeMillis()));
			});
			// commits the offset of record to broker.
			consumer.commitAsync();
		}
		consumer.close();
	}

}
