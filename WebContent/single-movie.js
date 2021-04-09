/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Name: " + resultData[0]["title"] +
        "</p>" + "<p>Release: " + resultData[0]["year"] + "</p>" +
        "<p>Director: " + resultData[0]["director"] + "</p>" +
        "<p>Rating: " + resultData[0]["rating"] + "</p>");


    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    const stars = resultData[0]["starName"].split(',');
    const starId = resultData[0]["starId"].split(',');
    const genre = resultData[0]["genreName"].split(',');
    let genreLength = genre.length;
    for(let i = 0; i < stars.length; i++){
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" +
            '<a href = "single-star.html?id=' + starId[i] + '">'
            + stars[i] + '</a>' + "</td>";
        if(i < genreLength){
            rowHTML += "<td>" + genre[i] + "</td>";
        }
        rowHTML += "<tr>";
        movieTableBodyElement.append(rowHTML);
    }
    //    rowHTML += "<tr>";
        /*rowHTML += "<td>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display title for the link text
            '</a>' +
            "</td>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";*/
     //   rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
    //    movieTableBodyElement.append(rowHTML);
    //}
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});