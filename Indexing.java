import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.text.SimpleDateFormat;

public class Indexing {
	public static boolean indexing(String inputPath, String indexPath) {
		boolean flag = true;
		final Path dataDir = Paths.get(inputPath);
		try {
			Directory index = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new EnglishAnalyzer();

			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			indexWriterConfig.setOpenMode(OpenMode.CREATE);
			IndexWriter indexWriter = new IndexWriter(index, indexWriterConfig);
			indexDirectory(indexWriter, dataDir);

			indexWriter.close();

			Date end = new Date();
		} catch (IOException e) {
			flag = false;
			System.out.println("Error in Indexing Class");
			System.out.println("Error in Search Query");
			System.out.println(e.getMessage());
		}
		return flag; 
	}

	public static void indexDirectory(final IndexWriter indexWriter, Path dataDir) throws IOException {
		if (Files.isDirectory(dataDir)) {
			Files.walkFileTree(dataDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String fileLowerCaseName = file.toString().toLowerCase();
					if (fileLowerCaseName.endsWith(".txt")) {
						try {
							indexDocument(indexWriter, file, attrs.lastModifiedTime().toMillis());
						} catch (IOException ignore) {
							// don't index files that can't be read.
						}
					} else {
//						System.out.println("This type of file will not be indexed" + file.toString());
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDocument(indexWriter, dataDir, Files.getLastModifiedTime(dataDir).toMillis());
		}
	}

	/** Indexes a single document */
	public static void indexDocument(IndexWriter writer, Path dataDir, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(dataDir)) {
			Document doc = new Document();

			try {
				Field pathField = new StringField("path", dataDir.toString(), Field.Store.YES);
				doc.add(pathField);

				String lastMod = new SimpleDateFormat("dd MMM yyyy HH:mm:ss ").format(lastModified);
				doc.add(new StringField("lastmodified", lastMod, Field.Store.YES));
				doc.add(new TextField("contents", new String(Files.readAllBytes(dataDir)) , Store.YES));

				writer.addDocument(doc);
			} catch (Exception e) {
				System.out.println("Error in Indexing Class - in indexDocument module");
				e.printStackTrace();
				System.out.println(e.getMessage());

			}
		}
	}
}
