package com.avatar.challenge.planner.challenge.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class DailyList {
    private final List<Daily> dailies = new ArrayList<>();

    public void add(Daily daily){
        if (dailies.contains(daily)){
           return;
        }
        dailies.add(daily);
    }

    public long numberOfCompleted(){
        return dailies.stream()
                .filter(daily -> daily.getIsComplete() == Boolean.TRUE)
                .count();
    }

    public int size() {
        return this.dailies.size();
    }
}
