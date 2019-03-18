# Video-Game-Recommender-System

The Video Game Recommender System shows top 10 games list based on user reviews. The Game Finder App use Metacritic Video Game Comments. To find top 10 games, the app use inverted term frequency for finiding similarities between user query and and game.

# Background
In the information retrieval, TF-IDF stands for term frequency-inverse document frequency. TF-IDF is tells us how important a particular word. Both tf and idf calculated separately.

After fiding tf-idf for each document and user query, we need to calculate consine similarity. Consine similiarity shows how similar two vectors. 

# Procedure

First, we need to load user comments and game titles.To load data, we will first load each line, then we will spilt the data to get title and user comments. 
        
        
        inputStream2 = getResources().openRawResource(R.raw.data2);// Loading each line
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2)); //Loading each line
        while (index < data2_line.size()) { //Spliting the data to get title and user reviews
            temp3=data2_line.get(index).split(",");
            game_number2.add(temp3[0]);
            title_data2.add(temp3[1]);
            platform2.add(temp3[2]);
            userscore2.add(temp3[3]);
            review2.add(temp3[4]);
            index++;
        }
        
        
After loading the loading the data, we will remove stopwords and tokenize each word. 

        while (index < title_data2.size()) {
            Pattern p =                                Pattern.compile("\\b(i|me|my|myself|we|our|ours|ourselves|you|your|yours|yourselves|he|him|she|her|himself|herself|his|" +
                    "it|its|it's|itself|they|them|their|theirs|themselves|what|which|who|whom|this|that" +
                    "|these|those|am|is|are|was|were|be|been|being|have|has|had|having|do|does|did|a|an|the|and" +
                    "|but|if|or|because|as|until|while|of|at|by|for|with|about|against|between|into|through|during|" +
                    "before|after|above|below|to|from|up|down|in|out|on|off|over|under|again|further|then|once|here|" +
                    "there|when|where|why|how|all|any|both|each|few|more|most|other|some|such|no|nor|not|only|own|same" +
                    "|so|than|too|very|can|will|just|don|should|now|;|\"|)\\b\\s?");
            Matcher m = p.matcher(review2.get(index).toLowerCase()); // converting everything into lowercase
            String s = m.replaceAll(" ");

            //Tokenizing
            StringTokenizer st1 = new StringTokenizer(s.toLowerCase());
            Vector<String> tokenized = new Vector<>();
            while (st1.hasMoreTokens()) {
                String temp_token = st1.nextToken();
                if (temp_token.equals("\"") || temp_token.equals(" \"")||temp_token.equals(";")||temp_token.equals("!")||temp_token.equals(" 've")
                        ||temp_token.equals("s")||temp_token.equals("-")||temp_token.equals("ve")||temp_token.equals("'")||temp_token.equals(".")) {
                }
                else {
                    tokenized.add(temp_token);}
            }
            tokenized_review.add(tokenized);
            index++;
        }
        
        
        
Now, we have created our token. We will use tokens to find TF_IDF value. We created two functions for tf and idf which will return tf and idf scores.
 //Calculating tf score
    public double tf(Vector<String> doc1, String user_query_term) {
        Double result = 0.0;
        int index = 0;
        while (index <doc1.size())
        {
            if (user_query_term.equalsIgnoreCase(doc1.get(index)))
                result++;
            index++;
        }
        if (result == 0)
            return 0.0;
        return (result/doc1.size()) ;
    }

    //Finding idf value
    public double idf(String term) {
        double count = 0;
        int index = 0;
        while (index<tokenized_review.size())
        {
            Vector<String> temp_query = tokenized_review.get(index);
            int index3=0;
            while (index3 < temp_query.size()){
                if (term.equalsIgnoreCase(temp_query.get(index3))) {
                    count++;
                    break;
                }
                index3++;
            }
            index++;
        }
        if (count == 0)
            return 1.0;
        return ((tokenized_review.size()) / count);
    }
    
We will use the following formula for TF_IDF score. Every every t token we will save the TF_IDF value. 
    
![Alt text](Image/formula1.JPG?raw=true "Title")

Now, we will find TF_IDF for user query. We tokenize user uery and find TF_IDF score. We will use below formula to find TF_IDF score.

![Alt text](Image/formula2.JPG?raw=true "Title")

After getting all the TF_IDF score, we have to find consine similarity score. Finiding cosine similarity for each document and user query is inefficient. For this reason, we will create posting list. Using posting list we can find top 10 documents for each user query token. To find the cosine_similarity we will create following cosine similarity caluclaton function.
    public double cos_sim(Vector<Double>Vector1, Vector<Double> Vector2) {
        double mul = 0.0,sum1 = 0.0,sum2=0.0,cos_sim_value = 0.0;

        //Converting Vector to array for cosine similarity
        double vector1[] = new double[Vector1.size()];
        for (int i=0; i<Vector1.size(); i++) {
            vector1[i] = Vector1.get(i);
        }
        double vector2[] = new double[Vector2.size()];
        for (int i=0; i<Vector2.size();i++) {
            vector2[i] = Vector2.get(i);
        }

        //Calculating cosine similarity
        for (int i=0;i<vector1.length;i++) {
            sum1= sum1+ Math.pow(vector1[i], 2);
            sum2= sum2+Math.pow(vector2[i], 2);
            mul= mul+(vector1[i] * vector2[i]);
        }
        //Square roots
        sum1 = Math.sqrt(sum1);
        sum2 = Math.sqrt(sum2);

        if (sum1 != 0.0 | sum2 != 0.0) {
            cos_sim_value = mul / (sum1 * sum2);
        } else {
            return 0.0;
        }
        return cos_sim_value;
    }
    
We will show top 10 results based on the above result.

Now, lets run the app. We will search funny game.


![Alt text](Image/1.JPG?raw=true "Title")

It will show top 10 games.

![Alt text](Image/2.JPG?raw=true "Title")
![Alt text](Image/3.JPG?raw=true "Title")
![Alt text](Image/4.JPG?raw=true "Title")
![Alt text](Image/5.JPG?raw=true "Title")

Environment:
Java Verison: java version "11.0.2" 2019-01-15 LTS
Java(TM) SE Runtime Environment: 18.9 (build 11.0.2+9-LTS)
OS: Windows 10
Android Studio version: 3.3

References:
https://github.com/AdnanOquaish/Cosine-similarity-Tf-Idf-/blob/master/DocumentParser.java
https://stackoverflow.com/questions/27685839/removing-stopwords-from-a-string-in-java

Dataset link:
https://www.kaggle.com/dahlia25/metacritic-video-game-comments#metacritic_game_info.csv




