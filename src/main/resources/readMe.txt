The created files are:
	- signatures.json. A JSON file where all the signatures created with the protectWithDS() method linked to the Phenopacket 	identifier are stored
	- sk_hybridEnc.json. The private key generated with the createKeySet() method of the HybridEncryption class is stored in this file
	- pk_hybridEnc.json. Like the previous file, this one has the public key generated from the private key 
	- [PhenopacketID].txt. This file stores the different hashes that can be computed in the Hashing class where [PhenopacketID] is the Phenopacket identifier that includes the element 
	- P-[PhenopacketID].json. Same as the above file, but in this case the different encryptions performed in HybridEncryption are stored along with the serialized Phenopacket in a byte array
