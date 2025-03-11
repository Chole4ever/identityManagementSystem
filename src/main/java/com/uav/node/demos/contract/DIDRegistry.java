package com.example.uav.contract;

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
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b5061119f806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80632421438c1461005157806324c483a5146100665780632a5d9dfa14610093578063784afc4e146100a6575b600080fd5b61006461005f366004610d62565b6100c9565b005b610079610074366004610e0f565b610373565b60405161008a959493929190610ea8565b60405180910390f35b6100646100a1366004610ef4565b6104c7565b6100b96100b4366004610e0f565b610701565b60405161008a9493929190610fd1565b60008451116100f35760405162461bcd60e51b81526004016100ea90611029565b60405180910390fd5b6000846040516101039190611056565b90815260405190819003602001902060020154156101585760405162461bcd60e51b8152602060048201526012602482015271111251081b9bdd081c9959da5cdd195c995960721b60448201526064016100ea565b336001600160a01b03166000856040516101729190611056565b908152604051908190036020019020600601546001600160a01b0316146101e55760405162461bcd60e51b815260206004820152602160248201527f596f7520617265206e6f7420746865206f776e6572206f6620746869732044496044820152601160fa1b60648201526084016100ea565b6040518060e001604052808581526020018481526020018381526020018281526020016000866040516102189190611056565b90815260200160405180910390206004015481526020014281526020016000866040516102459190611056565b9081526040805191829003602001909120600601546001600160a01b031690915251600090610275908790611056565b9081526020016040518091039020600082015181600001908051906020019061029f929190610ac0565b5060208281015180516102b89260018501920190610ac0565b50604082015180516102d4916002840191602090910190610b44565b50606082015180516102f0916003840191602090910190610b44565b506080820151600482015560a0820151600582015560c090910151600690910180546001600160a01b0319166001600160a01b039092169190911790556040517f28f5fd4a69b81c4b9812c12bd9a16c6f8f56d8bcc1f31cd01cb915bf3ce4f4f0906103659086908690869086904290611072565b60405180910390a150505050565b8051602081830181018051600082529282019190930120915280548190610399906110d2565b80601f01602080910402602001604051908101604052809291908181526020018280546103c5906110d2565b80156104125780601f106103e757610100808354040283529160200191610412565b820191906000526020600020905b8154815290600101906020018083116103f557829003601f168201915b505050505090806001018054610427906110d2565b80601f0160208091040260200160405190810160405280929190818152602001828054610453906110d2565b80156104a05780601f10610475576101008083540402835291602001916104a0565b820191906000526020600020905b81548152906001019060200180831161048357829003601f168201915b5050505060048301546005840154600690940154929390929091506001600160a01b031685565b60008351116104e85760405162461bcd60e51b81526004016100ea90611029565b60008251116105395760405162461bcd60e51b815260206004820152601a60248201527f5075626c6963206b65792063616e6e6f7420626520656d70747900000000000060448201526064016100ea565b6000836040516105499190611056565b90815260405190819003602001902060020154156105a95760405162461bcd60e51b815260206004820152601960248201527f44494420697320616c726561647920726567697374657265640000000000000060448201526064016100ea565b6040518060e00160405280848152602001604051806020016040528060008152508152602001838152602001828152602001428152602001428152602001336001600160a01b03168152506000846040516106049190611056565b9081526020016040518091039020600082015181600001908051906020019061062e929190610ac0565b5060208281015180516106479260018501920190610ac0565b5060408201518051610663916002840191602090910190610b44565b506060820151805161067f916003840191602090910190610b44565b506080820151600482015560a0820151600582015560c090910151600690910180546001600160a01b0319166001600160a01b039092169190911790556040517f97a09cfb69c56bcd4884f44a5a689e08ac2f6e0bbbb33231f0f2b980d68bad2a906106f4908590859085904290339061110d565b60405180910390a1505050565b60608060608060008551116107285760405162461bcd60e51b81526004016100ea90611029565b600080866040516107399190611056565b90815260200160405180910390206040518060e0016040529081600082018054610762906110d2565b80601f016020809104026020016040519081016040528092919081815260200182805461078e906110d2565b80156107db5780601f106107b0576101008083540402835291602001916107db565b820191906000526020600020905b8154815290600101906020018083116107be57829003601f168201915b505050505081526020016001820180546107f4906110d2565b80601f0160208091040260200160405190810160405280929190818152602001828054610820906110d2565b801561086d5780601f106108425761010080835404028352916020019161086d565b820191906000526020600020905b81548152906001019060200180831161085057829003601f168201915b5050505050815260200160028201805480602002602001604051908101604052809291908181526020016000905b828210156109475783829060005260206000200180546108ba906110d2565b80601f01602080910402602001604051908101604052809291908181526020018280546108e6906110d2565b80156109335780601f1061090857610100808354040283529160200191610933565b820191906000526020600020905b81548152906001019060200180831161091657829003601f168201915b50505050508152602001906001019061089b565b50505050815260200160038201805480602002602001604051908101604052809291908181526020016000905b82821015610a20578382906000526020600020018054610993906110d2565b80601f01602080910402602001604051908101604052809291908181526020018280546109bf906110d2565b8015610a0c5780601f106109e157610100808354040283529160200191610a0c565b820191906000526020600020905b8154815290600101906020018083116109ef57829003601f168201915b505050505081526020019060010190610974565b505050908252506004820154602082015260058201546040808301919091526006909201546001600160a01b031660609091015281015151909150610a9f5760405162461bcd60e51b8152602060048201526015602482015274111251081a5cc81b9bdd081c9959da5cdd195c9959605a1b60448201526064016100ea565b80516020820151604083015160609093015191989097509195509350915050565b828054610acc906110d2565b90600052602060002090601f016020900481019282610aee5760008555610b34565b82601f10610b0757805160ff1916838001178555610b34565b82800160010185558215610b34579182015b82811115610b34578251825591602001919060010190610b19565b50610b40929150610b9d565b5090565b828054828255906000526020600020908101928215610b91579160200282015b82811115610b915782518051610b81918491602090910190610ac0565b5091602001919060010190610b64565b50610b40929150610bb2565b5b80821115610b405760008155600101610b9e565b80821115610b40576000610bc68282610bcf565b50600101610bb2565b508054610bdb906110d2565b6000825580601f10610beb575050565b601f016020900490600052602060002090810190610c099190610b9d565b50565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610c4b57610c4b610c0c565b604052919050565b600082601f830112610c6457600080fd5b813567ffffffffffffffff811115610c7e57610c7e610c0c565b610c91601f8201601f1916602001610c22565b818152846020838601011115610ca657600080fd5b816020850160208301376000918101602001919091529392505050565b600082601f830112610cd457600080fd5b8135602067ffffffffffffffff80831115610cf157610cf1610c0c565b8260051b610d00838201610c22565b9384528581018301938381019088861115610d1a57600080fd5b84880192505b85831015610d5657823584811115610d385760008081fd5b610d468a87838c0101610c53565b8352509184019190840190610d20565b98975050505050505050565b60008060008060808587031215610d7857600080fd5b843567ffffffffffffffff80821115610d9057600080fd5b610d9c88838901610c53565b95506020870135915080821115610db257600080fd5b610dbe88838901610c53565b94506040870135915080821115610dd457600080fd5b610de088838901610cc3565b93506060870135915080821115610df657600080fd5b50610e0387828801610cc3565b91505092959194509250565b600060208284031215610e2157600080fd5b813567ffffffffffffffff811115610e3857600080fd5b610e4484828501610c53565b949350505050565b60005b83811015610e67578181015183820152602001610e4f565b83811115610e76576000848401525b50505050565b60008151808452610e94816020860160208601610e4c565b601f01601f19169290920160200192915050565b60a081526000610ebb60a0830188610e7c565b8281036020840152610ecd8188610e7c565b6040840196909652505060608101929092526001600160a01b031660809091015292915050565b600080600060608486031215610f0957600080fd5b833567ffffffffffffffff80821115610f2157600080fd5b610f2d87838801610c53565b94506020860135915080821115610f4357600080fd5b610f4f87838801610cc3565b93506040860135915080821115610f6557600080fd5b50610f7286828701610cc3565b9150509250925092565b600081518084526020808501808196508360051b8101915082860160005b85811015610fc4578284038952610fb2848351610e7c565b98850198935090840190600101610f9a565b5091979650505050505050565b608081526000610fe46080830187","610e7c565b8281036020840152610ff68187610e7c565b9050828103604084015261100a8186610f7c565b9050828103606084015261101e8185610f7c565b979650505050505050565b6020808252601390820152724449442063616e6e6f7420626520656d70747960681b604082015260600190565b60008251611068818460208701610e4c565b9190910192915050565b60a08152600061108560a0830188610e7c565b82810360208401526110978188610e7c565b905082810360408401526110ab8187610f7c565b905082810360608401526110bf8186610f7c565b9150508260808301529695505050505050565b600181811c908216806110e657607f821691505b6020821081141561110757634e487b7160e01b600052602260045260246000fd5b50919050565b60a08152600061112060a0830188610e7c565b82810360208401526111328188610f7c565b905082810360408401526111468187610f7c565b606084019590955250506001600160a01b0391909116608090910152939250505056fea2646970667358221220fe3142f1ef5f94a2e0948c75cb4f56c690dd626aa5f5e22e92d2bbfb420ad7fa64736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b506111a7806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80633e2f4c6514610051578063477c9550146100665780635ae55b0314610093578063e93f75e7146100a6575b600080fd5b61006461005f366004610d6a565b6100c9565b005b610079610074366004610e17565b610376565b60405161008a959493929190610eb0565b60405180910390f35b6100646100a1366004610efc565b6104ca565b6100b96100b4366004610e17565b610707565b60405161008a9493929190610fd9565b60008451116100f457604051636381e58960e11b81526004016100eb90611031565b60405180910390fd5b600084604051610104919061105e565b908152604051908190036020019020600201541561015a57604051636381e58960e11b8152602060048201526012602482015271111251081b9bdd081c9959da5cdd195c995960721b60448201526064016100eb565b336001600160a01b0316600085604051610174919061105e565b908152604051908190036020019020600601546001600160a01b0316146101e857604051636381e58960e11b815260206004820152602160248201527f596f7520617265206e6f7420746865206f776e6572206f6620746869732044496044820152601160fa1b60648201526084016100eb565b6040518060e0016040528085815260200184815260200183815260200182815260200160008660405161021b919061105e565b9081526020016040518091039020600401548152602001428152602001600086604051610248919061105e565b9081526040805191829003602001909120600601546001600160a01b03169091525160009061027890879061105e565b908152602001604051809103902060008201518160000190805190602001906102a2929190610ac8565b5060208281015180516102bb9260018501920190610ac8565b50604082015180516102d7916002840191602090910190610b4c565b50606082015180516102f3916003840191602090910190610b4c565b506080820151600482015560a0820151600582015560c090910151600690910180546001600160a01b0319166001600160a01b039092169190911790556040517fd739d1fbecb145db8d18d59365e5b2c54a9483aebca860539e929d4d24ab23e590610368908690869086908690429061107a565b60405180910390a150505050565b805160208183018101805160008252928201919093012091528054819061039c906110da565b80601f01602080910402602001604051908101604052809291908181526020018280546103c8906110da565b80156104155780601f106103ea57610100808354040283529160200191610415565b820191906000526020600020905b8154815290600101906020018083116103f857829003601f168201915b50505050509080600101805461042a906110da565b80601f0160208091040260200160405190810160405280929190818152602001828054610456906110da565b80156104a35780601f10610478576101008083540402835291602001916104a3565b820191906000526020600020905b81548152906001019060200180831161048657829003601f168201915b5050505060048301546005840154600690940154929390929091506001600160a01b031685565b60008351116104ec57604051636381e58960e11b81526004016100eb90611031565b600082511161053e57604051636381e58960e11b815260206004820152601a60248201527f5075626c6963206b65792063616e6e6f7420626520656d70747900000000000060448201526064016100eb565b60008360405161054e919061105e565b90815260405190819003602001902060020154156105af57604051636381e58960e11b815260206004820152601960248201527f44494420697320616c726561647920726567697374657265640000000000000060448201526064016100eb565b6040518060e00160405280848152602001604051806020016040528060008152508152602001838152602001828152602001428152602001428152602001336001600160a01b031681525060008460405161060a919061105e565b90815260200160405180910390206000820151816000019080519060200190610634929190610ac8565b50602082810151805161064d9260018501920190610ac8565b5060408201518051610669916002840191602090910190610b4c565b5060608201518051610685916003840191602090910190610b4c565b506080820151600482015560a0820151600582015560c090910151600690910180546001600160a01b0319166001600160a01b039092169190911790556040517f73ad3d8d67b9e5ef426a9b028f89e3316b4c2286c2becb7f407818569fa2c779906106fa9085908590859042903390611115565b60405180910390a1505050565b606080606080600085511161072f57604051636381e58960e11b81526004016100eb90611031565b60008086604051610740919061105e565b90815260200160405180910390206040518060e0016040529081600082018054610769906110da565b80601f0160208091040260200160405190810160405280929190818152602001828054610795906110da565b80156107e25780601f106107b7576101008083540402835291602001916107e2565b820191906000526020600020905b8154815290600101906020018083116107c557829003601f168201915b505050505081526020016001820180546107fb906110da565b80601f0160208091040260200160405190810160405280929190818152602001828054610827906110da565b80156108745780601f1061084957610100808354040283529160200191610874565b820191906000526020600020905b81548152906001019060200180831161085757829003601f168201915b5050505050815260200160028201805480602002602001604051908101604052809291908181526020016000905b8282101561094e5783829060005260206000200180546108c1906110da565b80601f01602080910402602001604051908101604052809291908181526020018280546108ed906110da565b801561093a5780601f1061090f5761010080835404028352916020019161093a565b820191906000526020600020905b81548152906001019060200180831161091d57829003601f168201915b5050505050815260200190600101906108a2565b50505050815260200160038201805480602002602001604051908101604052809291908181526020016000905b82821015610a2757838290600052602060002001805461099a906110da565b80601f01602080910402602001604051908101604052809291908181526020018280546109c6906110da565b8015610a135780601f106109e857610100808354040283529160200191610a13565b820191906000526020600020905b8154815290600101906020018083116109f657829003601f168201915b50505050508152602001906001019061097b565b505050908252506004820154602082015260058201546040808301919091526006909201546001600160a01b031660609091015281015151909150610aa757604051636381e58960e11b8152602060048201526015602482015274111251081a5cc81b9bdd081c9959da5cdd195c9959605a1b60448201526064016100eb565b80516020820151604083015160609093015191989097509195509350915050565b828054610ad4906110da565b90600052602060002090601f016020900481019282610af65760008555610b3c565b82601f10610b0f57805160ff1916838001178555610b3c565b82800160010185558215610b3c579182015b82811115610b3c578251825591602001919060010190610b21565b50610b48929150610ba5565b5090565b828054828255906000526020600020908101928215610b99579160200282015b82811115610b995782518051610b89918491602090910190610ac8565b5091602001919060010190610b6c565b50610b48929150610bba565b5b80821115610b485760008155600101610ba6565b80821115610b48576000610bce8282610bd7565b50600101610bba565b508054610be3906110da565b6000825580601f10610bf3575050565b601f016020900490600052602060002090810190610c119190610ba5565b50565b63b95aa35560e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610c5357610c53610c14565b604052919050565b600082601f830112610c6c57600080fd5b813567ffffffffffffffff811115610c8657610c86610c14565b610c99601f8201601f1916602001610c2a565b818152846020838601011115610cae57600080fd5b816020850160208301376000918101602001919091529392505050565b600082601f830112610cdc57600080fd5b8135602067ffffffffffffffff80831115610cf957610cf9610c14565b8260051b610d08838201610c2a565b9384528581018301938381019088861115610d2257600080fd5b84880192505b85831015610d5e57823584811115610d405760008081fd5b610d4e8a87838c0101610c5b565b8352509184019190840190610d28565b98975050505050505050565b60008060008060808587031215610d8057600080fd5b843567ffffffffffffffff80821115610d9857600080fd5b610da488838901610c5b565b95506020870135915080821115610dba57600080fd5b610dc688838901610c5b565b94506040870135915080821115610ddc57600080fd5b610de888838901610ccb565b93506060870135915080821115610dfe57600080fd5b50610e0b87828801610ccb565b91505092959194509250565b600060208284031215610e2957600080fd5b813567ffffffffffffffff811115610e4057600080fd5b610e4c84828501610c5b565b949350505050565b60005b83811015610e6f578181015183820152602001610e57565b83811115610e7e576000848401525b50505050565b60008151808452610e9c816020860160208601610e54565b601f01601f19169290920160200192915050565b60a081526000610ec360a0830188610e84565b8281036020840152610ed58188610e84565b6040840196909652505060608101929092526001600160a01b031660809091015292915050565b600080600060608486031215610f1157600080fd5b833567ffffffffffffffff80821115610f2957600080fd5b610f3587838801610c5b565b94506020860135915080821115610f4b57600080fd5b610f5787838801610ccb565b93506040860135915080821115610f6d57600080fd5b50610f7a86828701610ccb565b9150509250925092565b600081518084526020808501808196508360051b8101915082860160005b85811015610fcc578284038952610fba848351610e84565b98850198935090840190600101610fa2565b5091979650505050505050565b608081526000","610fec6080830187610e84565b8281036020840152610ffe8187610e84565b905082810360408401526110128186610f84565b905082810360608401526110268185610f84565b979650505050505050565b6020808252601390820152724449442063616e6e6f7420626520656d70747960681b604082015260600190565b60008251611070818460208701610e54565b9190910192915050565b60a08152600061108d60a0830188610e84565b828103602084015261109f8188610e84565b905082810360408401526110b38187610f84565b905082810360608401526110c78186610f84565b9150508260808301529695505050505050565b600181811c908216806110ee57607f821691505b6020821081141561110f5763b95aa35560e01b600052602260045260246000fd5b50919050565b60a08152600061112860a0830188610e84565b828103602084015261113a8188610f84565b9050828103604084015261114e8187610f84565b606084019590955250506001600160a01b0391909116608090910152939250505056fea2646970667358221220a4a9e80e33f8767d1116c91a4f54d9fd0aadb33ac7c6a70f5cf3a14eb58ad2e664736f6c634300080b0033"};

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
