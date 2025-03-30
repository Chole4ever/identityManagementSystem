// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract GDIDRegistry {
    // 定义结构体存储DID文档
    struct GDIDDocument {
        string gdid;
        bytes[] publicKeys;
        string[] serviceList;
        string[] didList;
        uint256 seq;              // 序列号应使用无符号类型
        uint256 created;          // 创建时间
        uint256 updated;          // 更新时间
        address owner;            // 文档所有者
    }

    mapping(string => GDIDDocument) public gdidDocuments;

    // 事件参数调整为匹配类型和顺序
    event GDIDRegistered(
        string gdid,
        bytes[] publicKeys,
        string[] serviceList,
        string[] didList,
        uint created,
        address owner,
        uint seq
    );

    event GDIDDocumentUpdated(
        string gdid,
        bytes[] publicKeys,
        string[] serviceList,
        string[] didList,
        uint256 timestamp,
        uint256 seq
    );

    // 注册GDID
    function registerGDID(
        string memory gdid_,
        bytes[] memory pkList_,
        string[] memory serverList_,
        string[] memory didLists_
    ) public {
        require(bytes(gdid_).length > 0, "GDID cannot be empty");
        require(pkList_.length > 0, "Public key cannot be empty");
        require(gdidDocuments[gdid_].publicKeys.length == 0, "GDID already registered");

        gdidDocuments[gdid_] = GDIDDocument({
            gdid: gdid_,
            publicKeys: pkList_,
            serviceList: serverList_,
            didList: didLists_,
            seq: 0,                         // 初始序列号设为0
            created: block.timestamp,
            updated: block.timestamp,
            owner: msg.sender
        });

        emit GDIDRegistered(
            gdid_,
            pkList_,
            serverList_,
            didLists_,
            block.timestamp,
            msg.sender,
            0
        );
    }

    // 更新GDID文档
    function updateGDIDDocument(
        string memory gdid_,
        bytes[] memory publicKeys_,
        string[] memory serviceList_,
        string[] memory didList_,
        uint256 seq                         // 序列号改为无符号
    ) public {
        require(bytes(gdid_).length > 0, "GDID cannot be empty");
        GDIDDocument storage doc = gdidDocuments[gdid_];
        require(doc.publicKeys.length > 0, "GDID not registered");
        require(doc.seq == seq, "Invalid sequence number");
        require(doc.owner == msg.sender, "Only owner can update");

        // 更新文档内容
        doc.publicKeys = publicKeys_;
        doc.serviceList = serviceList_;
        doc.didList = didList_;
        doc.updated = block.timestamp;
        doc.seq += 1;  // 序列号递增

        emit GDIDDocumentUpdated(
            gdid_,
            publicKeys_,
            serviceList_,
            didList_,
            block.timestamp,
            doc.seq
        );
    }

    // 查询GDID文档
    function getGDIDDocument(string memory gdid)
    public
    view
    returns (
        string memory gdid_,
        bytes[] memory publicKey,
        string[] memory serviceList,
        string[] memory didList,
        uint256 seq,
        uint256 created,
        uint256 updated,
        address owner
    )
    {
        require(bytes(gdid).length > 0, "GDID cannot be empty");
        GDIDDocument memory doc = gdidDocuments[gdid];
        require(doc.publicKeys.length > 0, "GDID not registered");

        return (
            doc.gdid,
            doc.publicKeys,
            doc.serviceList,
            doc.didList,
            doc.seq,
            doc.created,
            doc.updated,
            doc.owner
        );
    }
}
