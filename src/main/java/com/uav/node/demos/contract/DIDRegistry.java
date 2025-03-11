package com.uav.node.demos.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class DIDRegistry extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b506111a6806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80632421438c1461005157806324c483a5146100665780632a5d9dfa14610093578063784afc4e146100a6575b600080fd5b61006461005f366004610d66565b6100c9565b005b610079610074366004610e13565b610377565b60405161008a959493929190610eac565b60405180910390f35b6100646100a1366004610ef8565b6104cb565b6100b96100b4366004610e13565b610705565b60405161008a9493929190610fd8565b60008451116100f35760405162461bcd60e51b81526004016100ea90611030565b60405180910390fd5b60008085604051610104919061105d565b908152604051908190036020019020600201541161015c5760405162461bcd60e51b8152602060048201526015602482015274111251081a5cc81b9bdd081c9959da5cdd195c9959605a1b60448201526064016100ea565b336001600160a01b0316600085604051610176919061105d565b908152604051908190036020019020600601546001600160a01b0316146101e95760405162461bcd60e51b815260206004820152602160248201527f596f7520617265206e6f7420746865206f776e6572206f6620746869732044496044820152601160fa1b60648201526084016100ea565b6040518060e0016040528085815260200184815260200183815260200182815260200160008660405161021c919061105d565b9081526020016040518091039020600401548152602001428152602001600086604051610249919061105d565b9081526040805191829003602001909120600601546001600160a01b03169091525160009061027990879061105d565b908152602001604051809103902060008201518160000190805190602001906102a3929190610ac4565b5060208281015180516102bc9260018501920190610ac4565b50604082015180516102d8916002840191602090910190610b48565b50606082015180516102f4916003840191602090910190610b48565b506080820151600482015560a0820151600582015560c090910151600690910180546001600160a01b0319166001600160a01b039092169190911790556040517f28f5fd4a69b81c4b9812c12bd9a16c6f8f56d8bcc1f31cd01cb915bf3ce4f4f0906103699086908690869086904290611079565b60405180910390a150505050565b805160208183018101805160008252928201919093012091528054819061039d906110d9565b80601f01602080910402602001604051908101604052809291908181526020018280546103c9906110d9565b80156104165780601f106103eb57610100808354040283529160200191610416565b820191906000526020600020905b8154815290600101906020018083116103f957829003601f168201915b50505050509080600101805461042b906110d9565b80601f0160208091040260200160405190810160405280929190818152602001828054610457906110d9565b80156104a45780601f10610479576101008083540402835291602001916104a4565b820191906000526020600020905b81548152906001019060200180831161048757829003601f168201915b5050505060048301546005840154600690940154929390929091506001600160a01b031685565b60008351116104ec5760405162461bcd60e51b81526004016100ea90611030565b600082511161053d5760405162461bcd60e51b815260206004820152601a60248201527f5075626c6963206b65792063616e6e6f7420626520656d70747900000000000060448201526064016100ea565b60008360405161054d919061105d565b90815260405190819003602001902060020154156105ad5760405162461bcd60e51b815260206004820152601960248201527f44494420697320616c726561647920726567697374657265640000000000000060448201526064016100ea565b6040518060e00160405280848152602001604051806020016040528060008152508152602001838152602001828152602001428152602001428152602001336001600160a01b0316815250600084604051610608919061105d565b90815260200160405180910390206000820151816000019080519060200190610632929190610ac4565b50602082810151805161064b9260018501920190610ac4565b5060408201518051610667916002840191602090910190610b48565b5060608201518051610683916003840191602090910190610b48565b506080820151600482015560a0820151600582015560c090910151600690910180546001600160a01b0319166001600160a01b039092169190911790556040517f97a09cfb69c56bcd4884f44a5a689e08ac2f6e0bbbb33231f0f2b980d68bad2a906106f89085908590859042903390611114565b60405180910390a1505050565b606080606080600085511161072c5760405162461bcd60e51b81526004016100ea90611030565b6000808660405161073d919061105d565b90815260200160405180910390206040518060e0016040529081600082018054610766906110d9565b80601f0160208091040260200160405190810160405280929190818152602001828054610792906110d9565b80156107df5780601f106107b4576101008083540402835291602001916107df565b820191906000526020600020905b8154815290600101906020018083116107c257829003601f168201915b505050505081526020016001820180546107f8906110d9565b80601f0160208091040260200160405190810160405280929190818152602001828054610824906110d9565b80156108715780601f1061084657610100808354040283529160200191610871565b820191906000526020600020905b81548152906001019060200180831161085457829003601f168201915b5050505050815260200160028201805480602002602001604051908101604052809291908181526020016000905b8282101561094b5783829060005260206000200180546108be906110d9565b80601f01602080910402602001604051908101604052809291908181526020018280546108ea906110d9565b80156109375780601f1061090c57610100808354040283529160200191610937565b820191906000526020600020905b81548152906001019060200180831161091a57829003601f168201915b50505050508152602001906001019061089f565b50505050815260200160038201805480602002602001604051908101604052809291908181526020016000905b82821015610a24578382906000526020600020018054610997906110d9565b80601f01602080910402602001604051908101604052809291908181526020018280546109c3906110d9565b8015610a105780601f106109e557610100808354040283529160200191610a10565b820191906000526020600020905b8154815290600101906020018083116109f357829003601f168201915b505050505081526020019060010190610978565b505050908252506004820154602082015260058201546040808301919091526006909201546001600160a01b031660609091015281015151909150610aa35760405162461bcd60e51b8152602060048201526015602482015274111251081a5cc81b9bdd081c9959da5cdd195c9959605a1b60448201526064016100ea565b80516020820151604083015160609093015191989097509195509350915050565b828054610ad0906110d9565b90600052602060002090601f016020900481019282610af25760008555610b38565b82601f10610b0b57805160ff1916838001178555610b38565b82800160010185558215610b38579182015b82811115610b38578251825591602001919060010190610b1d565b50610b44929150610ba1565b5090565b828054828255906000526020600020908101928215610b95579160200282015b82811115610b955782518051610b85918491602090910190610ac4565b5091602001919060010190610b68565b50610b44929150610bb6565b5b80821115610b445760008155600101610ba2565b80821115610b44576000610bca8282610bd3565b50600101610bb6565b508054610bdf906110d9565b6000825580601f10610bef575050565b601f016020900490600052602060002090810190610c0d9190610ba1565b50565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610c4f57610c4f610c10565b604052919050565b600082601f830112610c6857600080fd5b813567ffffffffffffffff811115610c8257610c82610c10565b610c95601f8201601f1916602001610c26565b818152846020838601011115610caa57600080fd5b816020850160208301376000918101602001919091529392505050565b600082601f830112610cd857600080fd5b8135602067ffffffffffffffff80831115610cf557610cf5610c10565b8260051b610d04838201610c26565b9384528581018301938381019088861115610d1e57600080fd5b84880192505b85831015610d5a57823584811115610d3c5760008081fd5b610d4a8a87838c0101610c57565b8352509184019190840190610d24565b98975050505050505050565b60008060008060808587031215610d7c57600080fd5b843567ffffffffffffffff80821115610d9457600080fd5b610da088838901610c57565b95506020870135915080821115610db657600080fd5b610dc288838901610c57565b94506040870135915080821115610dd857600080fd5b610de488838901610cc7565b93506060870135915080821115610dfa57600080fd5b50610e0787828801610cc7565b91505092959194509250565b600060208284031215610e2557600080fd5b813567ffffffffffffffff811115610e3c57600080fd5b610e4884828501610c57565b949350505050565b60005b83811015610e6b578181015183820152602001610e53565b83811115610e7a576000848401525b50505050565b60008151808452610e98816020860160208601610e50565b601f01601f19169290920160200192915050565b60a081526000610ebf60a0830188610e80565b8281036020840152610ed18188610e80565b6040840196909652505060608101929092526001600160a01b031660809091015292915050565b600080600060608486031215610f0d57600080fd5b833567ffffffffffffffff80821115610f2557600080fd5b610f3187838801610c57565b94506020860135915080821115610f4757600080fd5b610f5387838801610cc7565b93506040860135915080821115610f6957600080fd5b50610f7686828701610cc7565b9150509250925092565b600082825180855260208086019550808260051b84010181860160005b84811015610fcb57601f19868403018952610fb9838351610e80565b98840198925090830190600101610f9d565b5090979650505050505050565b60808152600061","0feb6080830187610e80565b8281036020840152610ffd8187610e80565b905082810360408401526110118186610f80565b905082810360608401526110258185610f80565b979650505050505050565b6020808252601390820152724449442063616e6e6f7420626520656d70747960681b604082015260600190565b6000825161106f818460208701610e50565b9190910192915050565b60a08152600061108c60a0830188610e80565b828103602084015261109e8188610e80565b905082810360408401526110b28187610f80565b905082810360608401526110c68186610f80565b9150508260808301529695505050505050565b600181811c908216806110ed57607f821691505b6020821081141561110e57634e487b7160e01b600052602260045260246000fd5b50919050565b60a08152600061112760a0830188610e80565b82810360208401526111398188610f80565b9050828103604084015261114d8187610f80565b606084019590955250506001600160a01b0391909116608090910152939250505056fea2646970667358221220adbb99adb46ebb4632e586513b5b223942a6a9d6e0f5bd3647f1bb2eddab24ea64736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b506111ae806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80633e2f4c6514610051578063477c9550146100665780635ae55b0314610093578063e93f75e7146100a6575b600080fd5b61006461005f366004610d6e565b6100c9565b005b610079610074366004610e1b565b61037a565b60405161008a959493929190610eb4565b60405180910390f35b6100646100a1366004610f00565b6104ce565b6100b96100b4366004610e1b565b61070b565b60405161008a9493929190610fe0565b60008451116100f457604051636381e58960e11b81526004016100eb90611038565b60405180910390fd5b600080856040516101059190611065565b908152604051908190036020019020600201541161015e57604051636381e58960e11b8152602060048201526015602482015274111251081a5cc81b9bdd081c9959da5cdd195c9959605a1b60448201526064016100eb565b336001600160a01b03166000856040516101789190611065565b908152604051908190036020019020600601546001600160a01b0316146101ec57604051636381e58960e11b815260206004820152602160248201527f596f7520617265206e6f7420746865206f776e6572206f6620746869732044496044820152601160fa1b60648201526084016100eb565b6040518060e0016040528085815260200184815260200183815260200182815260200160008660405161021f9190611065565b908152602001604051809103902060040154815260200142815260200160008660405161024c9190611065565b9081526040805191829003602001909120600601546001600160a01b03169091525160009061027c908790611065565b908152602001604051809103902060008201518160000190805190602001906102a6929190610acc565b5060208281015180516102bf9260018501920190610acc565b50604082015180516102db916002840191602090910190610b50565b50606082015180516102f7916003840191602090910190610b50565b506080820151600482015560a0820151600582015560c090910151600690910180546001600160a01b0319166001600160a01b039092169190911790556040517fd739d1fbecb145db8d18d59365e5b2c54a9483aebca860539e929d4d24ab23e59061036c9086908690869086904290611081565b60405180910390a150505050565b80516020818301810180516000825292820191909301209152805481906103a0906110e1565b80601f01602080910402602001604051908101604052809291908181526020018280546103cc906110e1565b80156104195780601f106103ee57610100808354040283529160200191610419565b820191906000526020600020905b8154815290600101906020018083116103fc57829003601f168201915b50505050509080600101805461042e906110e1565b80601f016020809104026020016040519081016040528092919081815260200182805461045a906110e1565b80156104a75780601f1061047c576101008083540402835291602001916104a7565b820191906000526020600020905b81548152906001019060200180831161048a57829003601f168201915b5050505060048301546005840154600690940154929390929091506001600160a01b031685565b60008351116104f057604051636381e58960e11b81526004016100eb90611038565b600082511161054257604051636381e58960e11b815260206004820152601a60248201527f5075626c6963206b65792063616e6e6f7420626520656d70747900000000000060448201526064016100eb565b6000836040516105529190611065565b90815260405190819003602001902060020154156105b357604051636381e58960e11b815260206004820152601960248201527f44494420697320616c726561647920726567697374657265640000000000000060448201526064016100eb565b6040518060e00160405280848152602001604051806020016040528060008152508152602001838152602001828152602001428152602001428152602001336001600160a01b031681525060008460405161060e9190611065565b90815260200160405180910390206000820151816000019080519060200190610638929190610acc565b5060208281015180516106519260018501920190610acc565b506040820151805161066d916002840191602090910190610b50565b5060608201518051610689916003840191602090910190610b50565b506080820151600482015560a0820151600582015560c090910151600690910180546001600160a01b0319166001600160a01b039092169190911790556040517f73ad3d8d67b9e5ef426a9b028f89e3316b4c2286c2becb7f407818569fa2c779906106fe908590859085904290339061111c565b60405180910390a1505050565b606080606080600085511161073357604051636381e58960e11b81526004016100eb90611038565b600080866040516107449190611065565b90815260200160405180910390206040518060e001604052908160008201805461076d906110e1565b80601f0160208091040260200160405190810160405280929190818152602001828054610799906110e1565b80156107e65780601f106107bb576101008083540402835291602001916107e6565b820191906000526020600020905b8154815290600101906020018083116107c957829003601f168201915b505050505081526020016001820180546107ff906110e1565b80601f016020809104026020016040519081016040528092919081815260200182805461082b906110e1565b80156108785780601f1061084d57610100808354040283529160200191610878565b820191906000526020600020905b81548152906001019060200180831161085b57829003601f168201915b5050505050815260200160028201805480602002602001604051908101604052809291908181526020016000905b828210156109525783829060005260206000200180546108c5906110e1565b80601f01602080910402602001604051908101604052809291908181526020018280546108f1906110e1565b801561093e5780601f106109135761010080835404028352916020019161093e565b820191906000526020600020905b81548152906001019060200180831161092157829003601f168201915b5050505050815260200190600101906108a6565b50505050815260200160038201805480602002602001604051908101604052809291908181526020016000905b82821015610a2b57838290600052602060002001805461099e906110e1565b80601f01602080910402602001604051908101604052809291908181526020018280546109ca906110e1565b8015610a175780601f106109ec57610100808354040283529160200191610a17565b820191906000526020600020905b8154815290600101906020018083116109fa57829003601f168201915b50505050508152602001906001019061097f565b505050908252506004820154602082015260058201546040808301919091526006909201546001600160a01b031660609091015281015151909150610aab57604051636381e58960e11b8152602060048201526015602482015274111251081a5cc81b9bdd081c9959da5cdd195c9959605a1b60448201526064016100eb565b80516020820151604083015160609093015191989097509195509350915050565b828054610ad8906110e1565b90600052602060002090601f016020900481019282610afa5760008555610b40565b82601f10610b1357805160ff1916838001178555610b40565b82800160010185558215610b40579182015b82811115610b40578251825591602001919060010190610b25565b50610b4c929150610ba9565b5090565b828054828255906000526020600020908101928215610b9d579160200282015b82811115610b9d5782518051610b8d918491602090910190610acc565b5091602001919060010190610b70565b50610b4c929150610bbe565b5b80821115610b4c5760008155600101610baa565b80821115610b4c576000610bd28282610bdb565b50600101610bbe565b508054610be7906110e1565b6000825580601f10610bf7575050565b601f016020900490600052602060002090810190610c159190610ba9565b50565b63b95aa35560e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610c5757610c57610c18565b604052919050565b600082601f830112610c7057600080fd5b813567ffffffffffffffff811115610c8a57610c8a610c18565b610c9d601f8201601f1916602001610c2e565b818152846020838601011115610cb257600080fd5b816020850160208301376000918101602001919091529392505050565b600082601f830112610ce057600080fd5b8135602067ffffffffffffffff80831115610cfd57610cfd610c18565b8260051b610d0c838201610c2e565b9384528581018301938381019088861115610d2657600080fd5b84880192505b85831015610d6257823584811115610d445760008081fd5b610d528a87838c0101610c5f565b8352509184019190840190610d2c565b98975050505050505050565b60008060008060808587031215610d8457600080fd5b843567ffffffffffffffff80821115610d9c57600080fd5b610da888838901610c5f565b95506020870135915080821115610dbe57600080fd5b610dca88838901610c5f565b94506040870135915080821115610de057600080fd5b610dec88838901610ccf565b93506060870135915080821115610e0257600080fd5b50610e0f87828801610ccf565b91505092959194509250565b600060208284031215610e2d57600080fd5b813567ffffffffffffffff811115610e4457600080fd5b610e5084828501610c5f565b949350505050565b60005b83811015610e73578181015183820152602001610e5b565b83811115610e82576000848401525b50505050565b60008151808452610ea0816020860160208601610e58565b601f01601f19169290920160200192915050565b60a081526000610ec760a0830188610e88565b8281036020840152610ed98188610e88565b6040840196909652505060608101929092526001600160a01b031660809091015292915050565b600080600060608486031215610f1557600080fd5b833567ffffffffffffffff80821115610f2d57600080fd5b610f3987838801610c5f565b94506020860135915080821115610f4f57600080fd5b610f5b87838801610ccf565b93506040860135915080821115610f7157600080fd5b50610f7e86828701610ccf565b9150509250925092565b600082825180855260208086019550808260051b84010181860160005b84811015610fd357601f19868403018952610fc1838351610e88565b98840198925090830190600101610fa5565b509097965050505050505056","5b608081526000610ff36080830187610e88565b82810360208401526110058187610e88565b905082810360408401526110198186610f88565b9050828103606084015261102d8185610f88565b979650505050505050565b6020808252601390820152724449442063616e6e6f7420626520656d70747960681b604082015260600190565b60008251611077818460208701610e58565b9190910192915050565b60a08152600061109460a0830188610e88565b82810360208401526110a68188610e88565b905082810360408401526110ba8187610f88565b905082810360608401526110ce8186610f88565b9150508260808301529695505050505050565b600181811c908216806110f557607f821691505b602082108114156111165763b95aa35560e01b600052602260045260246000fd5b50919050565b60a08152600061112f60a0830188610e88565b82810360208401526111418188610f88565b905082810360408401526111558187610f88565b606084019590955250506001600160a01b0391909116608090910152939250505056fea2646970667358221220f98b55d3a80c3a43295574b0ed1b4e52f3069d56440d13a8bb07c68d4d633d7064736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"did\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"publicKeys\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"}],\"name\":\"DIDDocumentUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"did\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"publicKeys\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"DIDRegistered\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"name\":\"didDocuments\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"did\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"created\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"updated\",\"type\":\"uint256\"},{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"selector\":[616858533,1199347024],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":0,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"did\",\"type\":\"string\"}],\"name\":\"getDIDDocument\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"did_\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"publicKey\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"}],\"selector\":[2018180174,3913250279],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"did\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"publicKeys\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"}],\"name\":\"registerDID\",\"outputs\":[],\"selector\":[710778362,1524980483],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"did\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"gdid_\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"publicKeys_\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"serviceList_\",\"type\":\"string[]\"}],\"name\":\"updateDIDDocument\",\"outputs\":[],\"selector\":[606159756,1043287141],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_DIDDOCUMENTS = "didDocuments";

    public static final String FUNC_GETDIDDOCUMENT = "getDIDDocument";

    public static final String FUNC_REGISTERDID = "registerDID";

    public static final String FUNC_UPDATEDIDDOCUMENT = "updateDIDDocument";

    public static final Event DIDDOCUMENTUPDATED_EVENT = new Event("DIDDocumentUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DIDREGISTERED_EVENT = new Event("DIDRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    protected DIDRegistry(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<DIDDocumentUpdatedEventResponse> getDIDDocumentUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(DIDDOCUMENTUPDATED_EVENT, transactionReceipt);
        ArrayList<DIDDocumentUpdatedEventResponse> responses = new ArrayList<DIDDocumentUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            DIDDocumentUpdatedEventResponse typedResponse = new DIDDocumentUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.did = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.gdid = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.publicKeys = (List<String>) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.serviceList = (List<String>) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeDIDDocumentUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(DIDDOCUMENTUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeDIDDocumentUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(DIDDOCUMENTUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<DIDRegisteredEventResponse> getDIDRegisteredEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(DIDREGISTERED_EVENT, transactionReceipt);
        ArrayList<DIDRegisteredEventResponse> responses = new ArrayList<DIDRegisteredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            DIDRegisteredEventResponse typedResponse = new DIDRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.did = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.publicKeys = (List<String>) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.serviceList = (List<String>) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.owner = (String) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeDIDRegisteredEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(DIDREGISTERED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeDIDRegisteredEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(DIDREGISTERED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public Tuple5<String, String, BigInteger, BigInteger, String> didDocuments(String param0) throws
            ContractException {
        final Function function = new Function(FUNC_DIDDOCUMENTS, 
                Arrays.<Type>asList(new Utf8String(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<String, String, BigInteger, BigInteger, String>(
                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue(), 
                (BigInteger) results.get(3).getValue(), 
                (String) results.get(4).getValue());
    }

    public Function getMethodDidDocumentsRawFunction(String param0) throws ContractException {
        final Function function = new Function(FUNC_DIDDOCUMENTS, 
                Arrays.<Type>asList(new Utf8String(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        return function;
    }

    public Tuple4<String, String, List<String>, List<String>> getDIDDocument(String did) throws
            ContractException {
        final Function function = new Function(FUNC_GETDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(did)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple4<String, String, List<String>, List<String>>(
                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                convertToNative((List<Utf8String>) results.get(2).getValue()), 
                convertToNative((List<Utf8String>) results.get(3).getValue()));
    }

    public Function getMethodGetDIDDocumentRawFunction(String did) throws ContractException {
        final Function function = new Function(FUNC_GETDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(did)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}));
        return function;
    }

    public TransactionReceipt registerDID(String did, List<String> publicKeys,
            List<String> serviceList) {
        final Function function = new Function(
                FUNC_REGISTERDID, 
                Arrays.<Type>asList(new Utf8String(did),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodRegisterDIDRawFunction(String did, List<String> publicKeys,
            List<String> serviceList) throws ContractException {
        final Function function = new Function(FUNC_REGISTERDID, 
                Arrays.<Type>asList(new Utf8String(did),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList, Utf8String.class))),
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForRegisterDID(String did, List<String> publicKeys,
            List<String> serviceList) {
        final Function function = new Function(
                FUNC_REGISTERDID, 
                Arrays.<Type>asList(new Utf8String(did),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String registerDID(String did, List<String> publicKeys, List<String> serviceList,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_REGISTERDID, 
                Arrays.<Type>asList(new Utf8String(did),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple3<String, List<String>, List<String>> getRegisterDIDInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_REGISTERDID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, List<String>, List<String>>(

                (String) results.get(0).getValue(), 
                convertToNative((List<Utf8String>) results.get(1).getValue()), 
                convertToNative((List<Utf8String>) results.get(2).getValue())
                );
    }

    public TransactionReceipt updateDIDDocument(String did, String gdid_, List<String> publicKeys_,
            List<String> serviceList_) {
        final Function function = new Function(
                FUNC_UPDATEDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(did),
                new Utf8String(gdid_),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList_, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodUpdateDIDDocumentRawFunction(String did, String gdid_,
            List<String> publicKeys_, List<String> serviceList_) throws ContractException {
        final Function function = new Function(FUNC_UPDATEDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(did),
                new Utf8String(gdid_),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList_, Utf8String.class))),
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForUpdateDIDDocument(String did, String gdid_,
            List<String> publicKeys_, List<String> serviceList_) {
        final Function function = new Function(
                FUNC_UPDATEDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(did),
                new Utf8String(gdid_),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList_, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String updateDIDDocument(String did, String gdid_, List<String> publicKeys_,
            List<String> serviceList_, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_UPDATEDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(did),
                new Utf8String(gdid_),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList_, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple4<String, String, List<String>, List<String>> getUpdateDIDDocumentInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UPDATEDIDDOCUMENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, String, List<String>, List<String>>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                convertToNative((List<Utf8String>) results.get(2).getValue()), 
                convertToNative((List<Utf8String>) results.get(3).getValue())
                );
    }

    public static DIDRegistry load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new DIDRegistry(contractAddress, client, credential);
    }

    public static DIDRegistry deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(DIDRegistry.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }

    public static class DIDDocumentUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public String did;

        public String gdid;

        public List<String> publicKeys;

        public List<String> serviceList;

        public BigInteger timestamp;
    }

    public static class DIDRegisteredEventResponse {
        public TransactionReceipt.Logs log;

        public String did;

        public List<String> publicKeys;

        public List<String> serviceList;

        public BigInteger timestamp;

        public String owner;
    }
}
