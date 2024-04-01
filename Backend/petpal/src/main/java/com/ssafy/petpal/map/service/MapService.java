package com.ssafy.petpal.map.service;

import com.ssafy.petpal.map.dto.MapDto;
import com.ssafy.petpal.map.entity.Map;
import com.ssafy.petpal.map.entity.OriginMap;
import com.ssafy.petpal.map.repository.MapRepository;
import com.ssafy.petpal.map.repository.OriginMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Service
public class MapService {

    @Autowired
    private MapRepository mapRepository;

    private OriginMapRepository originMapRepository;
    public MapDto createMap(String homeId, String rawMapData) {
        String[][] dataArray = new String[700][700];
        StringTokenizer tokenizer = new StringTokenizer(rawMapData);
        int row = 0;
        int col = 0;
        while (tokenizer.hasMoreTokens()) {
            dataArray[row][col] = tokenizer.nextToken();
            col++;
            if (col == 700) {
                col = 0;
                row++;
            }
            if (row == 700) {
                break;
            }
        }

        OriginMap originMap = new OriginMap();
        originMap.setHomeId(Long.valueOf(homeId));
        originMap.setData(rawMapData);
        originMapRepository.save(originMap);

        String[][] largestRectangle = findLargestRectangleContaining100(dataArray);
        return saveMapData(homeId, largestRectangle);
    }

    private String[][] findLargestRectangleContaining100(String[][] dataArray) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int i = 0; i < dataArray.length; i++) {
            for (int j = 0; j < dataArray[i].length; j++) {
                if (dataArray[i][j].equals("100")) {
                    minX = Math.min(minX, j);
                    maxX = Math.max(maxX, j);
                    minY = Math.min(minY, i);
                    maxY = Math.max(maxY, i);
                }
            }
        }

        int height = maxY - minY + 1;
        int width = maxX - minX + 1;
        String[][] largestRectangle = new String[height][width];

        for (int i = minY; i <= maxY; i++) {
            System.arraycopy(dataArray[i], minX, largestRectangle[i - minY], 0, width);
        }

        return largestRectangle;
    }

    private MapDto saveMapData(String homeId, String[][] largestRectangle) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String[] row : largestRectangle) {
            for (String val : row) {
                stringBuilder.append(val).append(" ");
            }
            stringBuilder.append("\n");
        }

        Map mapEntity = new Map();
        mapEntity.setHomeId(Long.valueOf(homeId));
        mapEntity.setData(stringBuilder.toString().trim());
        mapRepository.save(mapEntity);

        return new MapDto(mapEntity.getHomeId(), mapEntity.getData());
    }

    private void printLargestRectangle(String[][] largestRectangle) {
        for (String[] row : largestRectangle) {
            for (String val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}
