Secure and Efficient Decentralized Identity Management System
This project presents a robust decentralized identity management system meticulously developed using Java and Solidity, offering a secure and flexible framework for digital identity verification and group authentication.

Key Functions:
1.Decentralized Identifier (DID) Registration: Facilitates the secure registration of individual DIDs, ensuring user control and data sovereignty.
2.Group Decentralized Identifier (GDID) Management: Enables the comprehensive management of GDIDs, encompassing:
  Secure Group Key Negotiation: Implements a sophisticated process for group key establishment among designated group members.
  GDDO (Group DID Document) On-chain Registration: Ensures the immutable recording of group identity objects on the blockchain for transparency and verifiability.
3.Advanced Group Key Generation: Leverages the power of cryptographic techniques by combining:
  Boneh-Lynn-Shacham (BLS) Signatures: For efficient and compact aggregate signatures.
  Distributed Key Generation (DKG): To enhance security and fault tolerance by preventing a single point of failure in key creation.
4，GDID-based Group Authentication: Provides a reliable mechanism for authenticating groups using their GDIDs, streamlining access control and verification processes.

This work draws inspiration from the research presented in:
T. Wang, Q. Cao, S. Zou and Y. Lu, "BGAS: Blockchain and Group Decentralized Identifiers Assisted Authentication Scheme for UAV Networks," 2024 IEEE 23rd International Conference on Trust, Security and Privacy in Computing and Communications (TrustCom), Sanya, China, 2024, pp. 1326-1335, doi: 10.1109/TrustCom63139.2024.00186.
