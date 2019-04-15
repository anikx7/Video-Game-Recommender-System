# Video-Game-Recommender-System: Game Finder

# Development Phase I: Search

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

# Output

Now, lets run the app. We will search funny game.


![Alt text](Image/1.JPG?raw=true "Title")

It will show top 10 games.

![Alt text](Image/2.JPG?raw=true "Title")
![Alt text](Image/3.JPG?raw=true "Title")
![Alt text](Image/4.JPG?raw=true "Title")
![Alt text](Image/5.JPG?raw=true "Title")


# Development Phase II: Classify
Classifying large dataset requires fast algorithm. To classify our existing documents from dataset, we will use Multinomial Naive Bayes Classidifer. The classify feature will classify user query based on training data.

Multinational Naive Bayes Classifier: 
It assumes every training data is independent. It also assumes each class is independent from each other.  Lets discuss step by step algorithm of multinomial naive bayes:
1. First we will load training data into the program from data-set. The training data-set contains details and genre of each document.
2. We will count the number of classes in the whole data-set.
3. We will perform stemming and lemmatization on the documents. We will use the same code that has been used in the search feature in the development phase I for stemming and lemmatization.
4. We will tokenize each document. We will also put each unique word in the bag of words. We also use same code that has been used in the search feature in the development phase I for tokenizing.
5. Now we will consider user query. The user query also need to tokenized classification. We will use same code used in the development phase I for usery query tokenization.
6. Now we are ready to use multinomial naive bayes classifier. We will begin by calculating Prior probabilities of each classes. To find prior probabilities, we will count how many document has a particular class and we will divide that the number by the total number of class.
        //Finding probabilty of each class
        
        double p_action= action_count/genre2.size();
        double p_sports=sports_count/genre2.size();
        double p_driving=driving_count/genre2.size();
        double p_puzzle=puzzle_count/genre2.size();
        double p_strategy=strategy_count/genre2.size();
        double p_adventure=adventure_count/genre2.size();
        double p_racing=racing_count/genre2.size();
        double p_fighting=fighting_count/genre2.size();
        double p_shooter=shooter_count/genre2.size();
        double p_compilation=compilation_count/genre2.size();
        double p_miscella = miscella_count/genre2.size();
        
 7. Now we will find conditional probabilties. After finding conditional probabities, we will find each class probabilities.
 
         while (index< user_query.size())
        {
            user_query_term = user_query.get(index);
            index2 = 0;
            word_count = 0;
            while (index2<action_index.size())
            {
                token_doc = tokenized_review.get(action_index.get(index2));
                index3 = 0;
                while (index3<token_doc.size())
                {
                    if (user_query_term.equalsIgnoreCase(token_doc.get(index3)))
                        word_count++;
                    index3++;
                }
                index2++;
            }
            cal_p = (word_count+1)/(action_count+unique_bag_of_words.size());
            user_p_action = user_p_action + cal_p;
            cal_p=0.0;
 8. The classifier will show top 3 most likely class for given query.
Sample Output:
The user query:





![Alt text](Image/c1.JPG?raw=true "Title")

Classification result:




![Alt text](Image/c2.JPG?raw=true "Title")

![Alt text](Image/c3.JPG?raw=true "Title")





# How to build it
1. Clone the whole repository
2. Extract Example1.zip folder in the Android Application folder
3. Download and install Android Studio Version 3.3
4. Download and install Java runtime
5. Open Android Studio
6. Import the Example1 extracted folder.
7. Build the project and run it

# Environment:
Java Verison: java version "11.0.2" 2019-01-15 LTS
Java(TM) SE Runtime Environment: 18.9 (build 11.0.2+9-LTS)
OS: Windows 10
Android Studio version: 3.3

# References:
Search

1. https://github.com/AdnanOquaish/Cosine-similarity-Tf-Idf-/blob/master/DocumentParser.java
2. https://stackoverflow.com/questions/27685839/removing-stopwords-from-a-string-in-java

Classifier

1. https://nlp.stanford.edu/IR-book/pdf/13bayes.pdf
2. https://www.youtube.com/watch?v=psHrcSacU9Y

Dataset link:
https://www.kaggle.com/dahlia25/metacritic-video-game-comments#metacritic_game_info.csv

# Blog post
1. Game Homepage: https://sites.google.com/view/videogamefinder
2. Blog post: https://sites.google.com/view/report1blog





