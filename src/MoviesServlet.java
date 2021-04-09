import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            //String query = "select * from (select genres.name as genreName,movies.id as movieId,movies.title as title,movies.year,movies.director,ratings.rating from genres inner join genres_in_movies on genres_in_movies.genreId = genres.id inner join movies on movies.id = genres_in_movies.movieId inner join ratings on ratings.movieId = movies.id order by rating desc limit 150) t1 inner join (select stars.name,stars.id as starId,movies.id from stars inner join stars_in_movies on stars_in_movies.starId = stars.id inner join movies on movies.id = stars_in_movies.movieId inner join ratings on ratings.movieId = movies.id order by rating desc limit 150) t2 on t1.movieId = t2.id;";
            String query = "select  t3.id,t3.title,t3.year,t3.director,t3.genre,t4.stars,t4.starId,t3.rating from (select id,title,year,director,rating, group_concat(name) as genre from (select genres.name,movies.id,movies.title,movies.year,movies.director,ratings.rating from genres inner join genres_in_movies on genres_in_movies.genreId = genres.id inner join movies on movies.id = genres_in_movies.movieId inner join ratings on ratings.movieId = movies.id order by rating desc limit 150)t1 group by id order by rating desc limit 20) t3 inner join (select id,title,year,director,rating, group_concat(name) as stars, group_concat(t2.starId) as starId from (select stars.name,stars.id as starId,movies.id,movies.title,movies.year,movies.director,ratings.rating from stars inner join stars_in_movies on stars_in_movies.starId = stars.id inner join movies on movies.id = stars_in_movies.movieId inner join ratings on ratings.movieId = movies.id order by rating desc limit 150)t2 group by id order by rating desc limit 20)t4 on t3.id = t4.id;";
            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();
            // Iterate through each row of rs

            while (rs.next()) {
                String movie_id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                float rating = rs.getFloat("rating");
                String genre = rs.getString("genre");
                String starName = rs.getString("stars");
                String starId = rs.getString("starId");
                // Create a JsonObject based on the data we retrieve from r
                JsonObject jsonObject;
                jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);
                jsonObject.addProperty("genre", genre);
                jsonObject.addProperty("star",starName);
                jsonObject.addProperty("starId",starId);
                jsonArray.add(jsonObject);

            }
            
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();

    }
}
