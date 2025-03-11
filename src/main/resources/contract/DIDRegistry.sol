// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract DIDRegistry {
    // 定义一个结构体来存储DID文档
    struct DIDDocument {
        string did;
        string gdid;
        string[] publicKeys;            // Public key list, allowing multiple keys associated with a user
        string[] serviceList;           // Service list, recording services or roles accessible to the user
        uint256 created;                // Document creation time
        uint256 updated;                // Document update time
        address owner; // DID文档的所有者地址
    }

    // 映射：DID地址 => DID文档
    mapping(string => DIDDocument) public didDocuments;

    // 事件：DID注册事件
    event DIDRegistered(string did, string[] publicKeys,  string[] serviceList, uint256 timestamp, address owner);

    // 事件：DID文档更新事件
    event DIDDocumentUpdated(string did, string gdid,string[] publicKeys,  string[] serviceList, uint256 timestamp);

    // 注册DID文档
    function registerDID(string memory did, string[] memory publicKeys, string[] memory serviceList) public {
        require(bytes(did).length > 0, "DID cannot be empty");
        require(publicKeys.length > 0, "Public key cannot be empty");

        // 确保DID尚未被注册
        require(didDocuments[did].publicKeys.length==0, "DID is already registered");

        // Store DID document with lists of public keys and services
        didDocuments[did] = DIDDocument({
            did: did,
            gdid:"",
            publicKeys: publicKeys,  // Array of public keys
            serviceList: serviceList,
            created: block.timestamp,  // Document creation time
            updated: block.timestamp,  // Document update time
            owner:msg.sender
        });

        // 触发注册事件
        emit DIDRegistered(did, publicKeys,serviceList, block.timestamp, msg.sender);
    }

    // 更新DID文档
    function updateDIDDocument(string memory did, string memory gdid_,string[] memory publicKeys_,string[] memory serviceList_) public {
        require(bytes(did).length > 0, "DID cannot be empty");
        require(didDocuments[did].publicKeys.length > 0, "DID is not registered");
        require(didDocuments[did].owner == msg.sender, "You are not the owner of this DID");

        // 更新DID文档
        didDocuments[did] = DIDDocument({
            did:did,
            gdid:gdid_,
            publicKeys: publicKeys_,  // Array of public keys
            serviceList: serviceList_,
            created: didDocuments[did].created,
            updated: block.timestamp, // Document update time
            owner:didDocuments[did].owner
        });

        emit DIDDocumentUpdated(did, gdid_,publicKeys_,serviceList_,block.timestamp);
    }

    // 查询DID文档
    function getDIDDocument(string memory did) public view returns (string memory did_,string memory gdid, string[] memory publicKey,string[] memory serviceList) {
        require(bytes(did).length > 0, "DID cannot be empty");

        // 获取DID文档
        DIDDocument memory didDoc = didDocuments[did];

        // 如果DID文档存在，则返回信息
        require(didDoc.publicKeys.length > 0, "DID is not registered");

        return (didDoc.did,didDoc.gdid,didDoc.publicKeys, didDoc.serviceList);
    }
}

