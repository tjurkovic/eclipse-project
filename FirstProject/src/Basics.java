import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.testng.Assert;

import files.ReusableMethods;
import files.payload;


public class Basics {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// validate if add place API is working as expected
		
		// three principles:
		// 1. given
		// take all input details
		
		// 2. when
		// submit the API - resource, HTTP methods
		
		// 3. Then
		// validate the response
		
		int code = 200;
		
		RestAssured.baseURI = "https://rahulshettyacademy.com";
		String response = given().log().all()
			.queryParam("key", "qaclick123").header("Content-Type", "application/json")
			.body(payload.AddPlace())
			.when().post("maps/api/place/add/json")
			.then().assertThat().statusCode(code) // check if status code is 200
			.body("scope", equalTo("APP")) // check if scope is "APP"
			.header("server", "Apache/2.4.41 (Ubuntu)") // check if server is Apache...
			.extract().response().asString();
		
		System.out.println(response);
		
		JsonPath jp = ReusableMethods.rawToJson(response); // for parsing JSon
		String placeId = jp.getString("place_id");
		
		System.out.println(placeId);
		
		String newAddress = "Hlavna 100, Kosice, Slovakia";
		
		// updating place with PUT API 
		// verifying if address changed correctly
		String responsePut = given().log().all()
		.queryParam("key", "qaclick123").queryParam("place_id", placeId)
		.header("Content-Type", "application/json")
		.body("{\r\n"
				+ "\"place_id\":\""+ placeId +"\",\r\n"
				+ "\"address\":\""+ newAddress +"\",\r\n"
				+ "\"key\":\"qaclick123\"\r\n"
				+ "}")
		.when().put("maps/api/place/update/json")
		.then().assertThat().statusCode(code).body("msg", equalTo("Address successfully updated"))
		.extract().response().asString();
		
		System.out.println(responsePut);
		
		// verifying changed place with GET API
		String responseGet = given().log().all()
				.queryParam("key", "qaclick123").queryParam("place_id", placeId)
				.body("")
				.when().get("maps/api/place/get/json")
				.then().assertThat().statusCode(code)
//				.body("address", equalTo(newAddress)) // check if address correctly changed 
				.extract().response().asString();
		JsonPath jp2 = ReusableMethods.rawToJson(responseGet);
		String actualAddress = jp2.getString("address");
		
		// using TestNG Library:
		Assert.assertEquals(actualAddress, newAddress);
		
		System.out.println(responseGet);
		
	}

}
