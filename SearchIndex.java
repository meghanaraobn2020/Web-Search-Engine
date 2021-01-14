import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

/** Simple command-line based search. */
public class SearchIndex {

	public static HashMap<Integer, ArrayList<String>>  searching(String indexPath, String queryInput) throws Exception {
		HashMap<Integer, ArrayList<String>> dictionary = new HashMap<Integer, ArrayList<String>>();
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new EnglishAnalyzer();

			QueryParser queryParser = new QueryParser("contents", analyzer);

			int num_hits = 10;
			Query query;
			if (queryInput.trim().isEmpty() == false) {
				try {
					query = queryParser.parse(QueryParser.escape(queryInput));
					TopDocs docs = searcher.search(query, num_hits);
					ScoreDoc[] hits = docs.scoreDocs;

					Formatter formatter = new SimpleHTMLFormatter();
					QueryScorer scorer = new QueryScorer(query);
					Highlighter highlighter = new Highlighter(formatter, scorer);
					Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 10);
					highlighter.setTextFragmenter(fragmenter);


					if (hits != null && hits.length > 0) {

//						System.out.println("Total " + hits.length + " documents found");
//						System.out.println("______________Top " + hits.length + " Relevant Documents______________ ");

						for (int i = 0; i < hits.length; ++i) {
							ArrayList<String> fragArray = new ArrayList<String>();
							ArrayList<String> stringArray = new ArrayList<String>();
							int docId = hits[i].doc;
							Document doc = searcher.doc(docId);
							String filepath = doc.get("path");
							File file = new File(filepath);
							String filename = file.getName();
							String text = doc.get("contents");
							TokenStream stream = TokenSources.getAnyTokenStream(reader, docId, "contents", analyzer);
							String[] frags = highlighter.getBestFragments(stream, text, 10);

//							System.out.println("\nRank: " + (i + 1) + "\nPath: " + filepath + " \nLast Modified: "
//									+ doc.get("lastmodified") + "\nRelevance Score: " + hits[i].score);

							stringArray.add(Integer.toString(i+1));
							stringArray.add(filename.substring(0, filename.length()-4));
//							System.out.println("filePath: " + filepath);
							stringArray.add(doc.get("lastmodified"));
							stringArray.add(Float.toString(hits[i].score));

							for (String frag : frags)
							{
//								System.out.println("=======================");
//								System.out.println(frag);
								fragArray.add(frag);
							}

							String tempFragArray = fragArray.toString();

							tempFragArray = tempFragArray.replaceAll("[\\[\\]]", "");

							stringArray.add(tempFragArray);
							//Add image ----Diana
							String pathImage = getImage(filepath);
							stringArray.add(pathImage);
							//---
							dictionary.put(i, stringArray);
						}
					} else
						System.out.println("No results found\n");
//					System.out.println("______________________________________________________\n\n");
				}
				catch (Exception e) {
					System.out.println("Wrong Input");
				}
			} else {
				System.out.println("Wrong Input\n");
			}

		} catch (Exception e) {
			System.out.println("Error in Search Index Class");
			e.printStackTrace();
		}
		System.out.println("end search");
		return dictionary;
	}

	public static String getImage(String pathFile) {
//		System.out.println("GetImage method");
		String imagePath = "";
		try {
			File fileInfo  = new File (pathFile);
			String nameFile = fileInfo.getName().substring(0, fileInfo.getName().indexOf("."));
			List<Path> f = Files.list(Paths.get(pathFile).getParent()).filter(path -> path.toString().contains(nameFile)).collect(Collectors.toList());
			for (Path p: f) {
				//Get files with the same name and that don't finish with .txt
				if (!p.toString().endsWith(".txt")){
					imagePath = p.toString();					
				}
			}
		}
		catch(Exception e) {
			
		}
		return imagePath;
	}
}
