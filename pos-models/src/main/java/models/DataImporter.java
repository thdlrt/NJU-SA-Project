package models;

import models.model.Product;
import models.service.ProductService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class DataImporter implements CommandLineRunner {

    @Autowired
    private ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        //importData("D:\\下载\\浏览器\\meta_Books.jsonl");
    }

    public void importData(String filePath) {
        JsonFactory jsonFactory = new JsonFactory();
        try (JsonParser jsonParser = jsonFactory.createParser(new File(filePath))) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                Product product = new Product();

                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = jsonParser.getCurrentName();
                    jsonParser.nextToken();

                    if (fieldName == null) {
                        continue;
                    }

                    switch (fieldName) {
                        case "title":
                            product.setName(jsonParser.getValueAsString());
                            break;
                        case "price":
                            product.setPrice(jsonParser.getValueAsDouble());
                            break;
                        case "images":
                            if (jsonParser.currentToken() == JsonToken.START_ARRAY) {
                                jsonParser.nextToken(); // Move to first element in array
                                if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
                                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                        String imageField = jsonParser.getCurrentName();
                                        jsonParser.nextToken();
                                        if ("large".equals(imageField)) {
                                            product.setImg(jsonParser.getValueAsString());
                                        }
                                    }
                                }
                            }
                            break;
                        case "main_category":
                            product.setCategory(jsonParser.getValueAsString());
                            break;
                        default:
                            jsonParser.skipChildren();
                    }
                }

                // 检查必填字段是否存在
                if (product.getName() != null && product.getCategory() != null && product.getImg() != null) {
                    productService.saveProduct(product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
