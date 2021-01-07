import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
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
import org.apache.*;

/** Simple command-line based search. */
public class SearchIndex {

	public static HashMap<Integer, ArrayList<String>>  searching(String indexPath, String queryInput) throws Exception {
		
		
		HashMap<Integer, ArrayList<String>> dictionary = new HashMap<Integer, ArrayList<String>>();

		
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new EnglishAnalyzer();

			//BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
//          MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"contents", "title"}, analyzer);
			QueryParser queryParser = new QueryParser("contents", analyzer);

			//String queryInput = "";
			//int flag = 0;
			int num_hits = 10;
			Query query;
			//while (flag != 1) {
				//System.out.println("\nEnter query: ");
				//queryInput = bReader.readLine();
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
							
							System.out.println("Total " + hits.length + " documents found");
							System.out.println("______________Top " + hits.length + " Relevant Documents______________ ");
							
							for (int i = 0; i < hits.length; ++i) {
								ArrayList<String> fragArray = new ArrayList<String>();
								ArrayList<String> stringArray = new ArrayList<String>();
								int docId = hits[i].doc;
								Document doc = searcher.doc(docId);
								String filepath = doc.get("path");
								
								String text = doc.get("contents");
								TokenStream stream = TokenSources.getAnyTokenStream(reader, docId, "contents", analyzer);
								
								
								String[] frags = highlighter.getBestFragments(stream, text, 10);

								System.out.println("\nRank: " + (i + 1) + "\nPath: " + filepath + " \nLast Modified: "
										+ doc.get("lastmodified") + "\nRelevance Score: " + hits[i].score);
								
								stringArray.add(Integer.toString(i+1));
								stringArray.add(filepath);
								stringArray.add(doc.get("lastmodified"));
								stringArray.add(Float.toString(hits[i].score));
								
								for (String frag : frags) 
					            {
					                System.out.println("=======================");
					                System.out.println(frag);
					                fragArray.add(frag);
					            }
								
								String tempFragArray = fragArray.toString();
								
								tempFragArray = tempFragArray.replaceAll("[\\[\\]]", "");

								stringArray.add(tempFragArray);
								dictionary.put(i, stringArray);
								
								
							
								
								File file = new File(filepath);
								String filename = file.getName();
								if (filename.endsWith(".htm") || filename.endsWith(".html")) {
									System.out.println("Title: " + doc.get("title"));
									System.out.println("Summary: " + doc.get("summary"));
								}
							}
						} else
							System.out.println("No results found\n");
						System.out.println("______________________________________________________\n\n");
					}

					catch (Exception e) {
						System.out.println("Wrong Input");
					}

				} else {
					System.out.println("Wrong Input\n");
				}
				
				/*System.out.println("Do you want to continue searching?");
				System.out.print("Press y to continue:  ");
				String UserInput = bReader.readLine();
				if (UserInput.equalsIgnoreCase("y"))
					flag = 0;
				else
					flag = 1;
			}*/

		} catch (Exception e) {
			System.out.println("Error in Search Index Class");
			e.printStackTrace();
		}
		
		return dictionary;
	}

}
