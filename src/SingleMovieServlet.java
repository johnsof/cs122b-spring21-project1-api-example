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
import java.sql.PreparedStatement;
import java.sql.ResultSet;


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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            //String query = "select * from movies where movies.id = ?;";
            //String query = "select movies.id,movies.title,movies.year,movies.director,stars.id,group_concat( distinct genres.name) as genreName,ratings.rating,group_concat(stars.name) as starName from movies,stars_in_movies,genres_in_movies,ratings,stars,genres where movies.id = ? and stars_in_movies.movieId = ? and genres_in_movies.movieId = ? and ratings.movieId = ? and genres_in_movies.genreId = genres.id and stars_in_movies.starId = stars.id;";
            String query = "select movies.id,movies.title,movies.year,movies.director,group_concat(distinct stars.id) as starId,group_concat( distinct genres.name) as genreName,ratings.rating,group_concat(distinct stars.name) as starName from movies,stars_in_movies,genres_in_movies,ratings,stars,genres where movies.id = ? and stars_in_movies.movieId = ? and genres_in_movies.movieId = ? and ratings.movieId = ? and genres_in_movies.genreId = genres.id and stars_in_movies.starId = stars.id;";
            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);
            statement.setString(2, id);
            statement.setString(3, id);
            statement.setString(4, id);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movieId = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String starId = rs.getString("starId");
                String genreName = rs.getString("genreName");
                String starName = rs.getString("starName");
                float rating = rs.getFloat("rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("title",title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director",director);
                jsonObject.addProperty("starId",starId);
                jsonObject.addProperty("genreName",genreName);
                jsonObject.addProperty("starName",starName);
                jsonObject.addProperty("rating",rating);

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
