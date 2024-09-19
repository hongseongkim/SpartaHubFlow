package com.sparta.hub.domain.hub.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.io.ClassPathResource;

public class HubDataLoader {
    public static List<HubData> getHubsData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ClassPathResource("hubs.json").getInputStream();
        return mapper.readValue(is, new TypeReference<List<HubData>>(){});
    }
}
