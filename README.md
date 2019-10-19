# mm-ADT: Virtual Machine

<img src="http://www.mm-adt.org/assets/images/mm-adt-logo.png" align="left" width="150px"> mm-ADT&#8482; is a distributed computing virtual machine aimed at integrating data storage systems, processing engines, and query languages. A collection of language-agnostic interfaces is made available to each technology community and when a valid implementaiton of said interfaces is created, the interfacing component is considered *mm-ADT compliant* and can faithfully interoperate with any other mm-ADT compliant component. In this manner, query language developers can develop languages irrespective of the underlying storage system that will ultimately be manipulated by the language. Similarly, processing engine developers can have their processors programmed by any query language for data stored in any storage system. Finally, storage systems (such as databases) immediately support any all mm-ADT compliant query languages and processors in support of the varigated requirements of their end users' data processing requirements.