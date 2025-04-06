package com.uav.node.demos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

@Data
public class Claim {

    List<UAVMember> members = new ArrayList<>();
    MissionProfile missionProfile;
    SwarmTopology swarmTopology;
    List<String> serverList = new ArrayList<>();

    @Data
    public static class UAVMember {
        @JsonProperty("did")
        String did;
        @JsonProperty("role")
        String role;
    }
    public static class SwarmTopology {
        @JsonProperty("type")
        String networkType;
        @JsonProperty("routingProtocol")
        String protocol;
        @JsonProperty("maxHops")
        int maxHop;
    }
    static class MissionProfile {
        @JsonProperty("type")
        String missionType;
        @JsonProperty("priority")
        String priorityLevel;
    }
    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
    // 反序列化
    public Claim fromJson(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Claim.class);
    }

    public Claim()
    {
        // 设置网络拓扑
        SwarmTopology topology = new SwarmTopology();
        topology.networkType = "Mesh";
        topology.protocol = "OLSR";
        topology.maxHop = 3;
        this.swarmTopology = topology;

        // 设置任务配置
        MissionProfile mission = new MissionProfile();
        mission.missionType = "Surveillance";
        mission.priorityLevel = "High";
        this.missionProfile = mission;

        // 添加服务器列表
        this.serverList.add("10.0.0.1:10000");
       // this.serverList.add("10.0.0.2:8080");

        // 填充无人机成员
        UAVMember leader = new UAVMember();
        leader.did = "did:UAV:4299393459333579452";
        leader.role = "Leader";
        this.members.add(leader);
    }

    public static void main(String[] args) {
        try {
            // 实例化主对象
            Claim claim = new Claim();

            // 填充无人机成员
            UAVMember leader = new UAVMember();
            leader.did = "did:uav:001";
            leader.role = "Leader";
            claim.members.add(leader);

            UAVMember follower = new UAVMember();
            follower.did = "did:uav:002";
            follower.role = "Follower";
            claim.members.add(follower);

            // 设置网络拓扑
            SwarmTopology topology = new SwarmTopology();
            topology.networkType = "Mesh";
            topology.protocol = "OLSR";
            topology.maxHop = 3;
            claim.swarmTopology = topology;

            // 设置任务配置
            MissionProfile mission = new MissionProfile();
            mission.missionType = "Surveillance";
            mission.priorityLevel = "High";
            claim.missionProfile = mission;

            // 添加服务器列表
            claim.serverList.add("10.0.0.1:8080");
            claim.serverList.add("10.0.0.2:8080");

            // 生成JSON并打印
            System.out.println("Generated JSON:\n" + claim.toJson());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}