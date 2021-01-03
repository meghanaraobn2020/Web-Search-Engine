import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
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


/** Simple command-line based search. */
public class SearchIndex {

    public static String[] searching(String indexPath,String query_string) throws Exception {
        String[] strArray = new String[10];
        try{
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new EnglishAnalyzer();

            BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"contents","path"}, analyzer);
            //QueryParser queryParser = new QueryParser("contents", analyzer);

            String queryInput = "";

            int flag = 0;
            int num_hits = 10;
            Query query;

            queryInput = query_string;
            if (queryInput.trim().isEmpty() == false) {
                try {
                    query = queryParser.parse(QueryParser.escape(queryInput));
                    System.out.println("Query: "+query);
                    TopDocs docs = searcher.search(query, num_hits);
                    ScoreDoc[] hits = docs.scoreDocs;
                    if (hits != null && hits.length > 0) {
                        System.out.println("Total " + hits.length + " documents found");
                        System.out.println("______________Top " + hits.length + " Relevant Documents______________ ");
                        for (int i = 0; i < hits.length; ++i) {
                            int docId = hits[i].doc;
                            Document doc = searcher.doc(docId);
                            String filepath = doc.get("path");
                            System.out.println("\nRank: " + (i + 1) + "\nPath: " + filepath + " \nLast Modified: " + doc.get("lastmodified") + "\nRelevance Score: " + hits[i].score + "\n"+ doc.get("contents"));
                            String temp = "\nPath: " + filepath + " \nLast Modified: " + doc.get("lastmodified") + "\nRelevance Score: " + hits[i].score + doc.toString();
                            strArray[i] = temp;
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

                catch(Exception e){
                    System.out.println("Wrong Input");
                }

            }
            else{
                System.out.println("Wrong Input\n");
            }

        }
        catch (Exception e) {
            System.out.println("Error in Search Index Class");
            e.printStackTrace();
        }
        return strArray;
    }


}