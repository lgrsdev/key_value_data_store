package kvds.server.ds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;
 
public class DataStoreDao {
	
	private static final String DATA_STORE_PATH = "data";

	Deque<String> readFromDisk(String key) throws IOException { 
		Deque<String> values = new ConcurrentLinkedDeque<>();
		if (new File(getFile(key).toString()).exists()) {
			try (Stream<String> stream = Files.lines(getFile(key))) {
				stream.forEach(values::add);
			}
		}
		return values;
	} 

	void writeToDisc(String key, Deque<String> first) {
		new File(DATA_STORE_PATH + File.separator + key).delete();
		first.forEach((str) -> {
			try (BufferedWriter writer = Files.newBufferedWriter(getFile(key), StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);) {
				writer.write(str);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		});
	}
	
	DirectoryStream<Path> readFilesFromDisk() throws IOException {
		return Files.newDirectoryStream(Paths.get(DATA_STORE_PATH), file -> !Files.isHidden(file));
	}

	private Path getFile(String key) {
		return Paths.get(DATA_STORE_PATH + File.separator + key);
	}

}
