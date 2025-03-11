package com.uav.node.demos.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple8;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class GDIDRegistry extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b50611324806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063105fbfe01461005157806346f6c81114610081578063b0c7fa1e14610096578063d5da160c146100ba575b600080fd5b61006461005f366004610cb2565b6100cd565b604051610078989796959493929190610dee565b60405180910390f35b61009461008f366004610fb8565b61050e565b005b6100a96100a4366004610cb2565b610706565b60405161007895949392919061106d565b6100946100c83660046110ad565b6107d1565b60608060608060008060008060008951116101035760405162461bcd60e51b81526004016100fa9061117f565b60405180910390fd5b6000808a60405161011491906111ad565b90815260200160405180910390206040518061010001604052908160008201805461013e906111c9565b80601f016020809104026020016040519081016040528092919081815260200182805461016a906111c9565b80156101b75780601f1061018c576101008083540402835291602001916101b7565b820191906000526020600020905b81548152906001019060200180831161019a57829003601f168201915b5050505050815260200160018201805480602002602001604051908101604052809291908181526020016000905b82821015610291578382906000526020600020018054610204906111c9565b80601f0160208091040260200160405190810160405280929190818152602001828054610230906111c9565b801561027d5780601f106102525761010080835404028352916020019161027d565b820191906000526020600020905b81548152906001019060200180831161026057829003601f168201915b5050505050815260200190600101906101e5565b50505050815260200160028201805480602002602001604051908101604052809291908181526020016000905b8282101561036a5783829060005260206000200180546102dd906111c9565b80601f0160208091040260200160405190810160405280929190818152602001828054610309906111c9565b80156103565780601f1061032b57610100808354040283529160200191610356565b820191906000526020600020905b81548152906001019060200180831161033957829003601f168201915b5050505050815260200190600101906102be565b50505050815260200160038201805480602002602001604051908101604052809291908181526020016000905b828210156104435783829060005260206000200180546103b6906111c9565b80601f01602080910402602001604051908101604052809291908181526020018280546103e2906111c9565b801561042f5780601f106104045761010080835404028352916020019161042f565b820191906000526020600020905b81548152906001019060200180831161041257829003601f168201915b505050505081526020019060010190610397565b50505090825250600482015460208083019190915260058301546040830152600683015460608301526007909201546001600160a01b0316608090910152810151519091506104ca5760405162461bcd60e51b815260206004820152601360248201527211d11251081b9bdd081c9959da5cdd195c9959606a1b60448201526064016100fa565b806000015181602001518260400151836060015184608001518560a001518660c001518760e001519850985098509850985098509850985050919395975091939597565b600085511161052f5760405162461bcd60e51b81526004016100fa9061117f565b6000808660405161054091906111ad565b90815260405190819003602001902060018101549091506105995760405162461bcd60e51b815260206004820152601360248201527211d11251081b9bdd081c9959da5cdd195c9959606a1b60448201526064016100fa565b818160040154146105ec5760405162461bcd60e51b815260206004820152601760248201527f496e76616c69642073657175656e6365206e756d62657200000000000000000060448201526064016100fa565b60078101546001600160a01b031633146106405760405162461bcd60e51b81526020600482015260156024820152744f6e6c79206f776e65722063616e2075706461746560581b60448201526064016100fa565b84516106559060018301906020880190610a2a565b50835161066b9060028301906020870190610a87565b5082516106819060038301906020860190610a87565b5042816006018190555060018160040160008282546106a09190611204565b90915550506040516106b39087906111ad565b60405180910390207fc98a85659fd09f1abc684fd3919bfa43dae7d88bb53c2abc23b180fab43d461d8686864286600401546040516106f695949392919061122a565b60405180910390a2505050505050565b805160208183018101805160008252928201919093012091528054819061072c906111c9565b80601f0160208091040260200160405190810160405280929190818152602001828054610758906111c9565b80156107a55780601f1061077a576101008083540402835291602001916107a5565b820191906000526020600020905b81548152906001019060200180831161078857829003601f168201915b50505050600483015460058401546006850154600790950154939491939092506001600160a01b031685565b60008551116107f25760405162461bcd60e51b81526004016100fa9061117f565b60008351116108435760405162461bcd60e51b815260206004820152601a60248201527f5075626c6963206b65792063616e6e6f7420626520656d70747900000000000060448201526064016100fa565b60008560405161085391906111ad565b90815260405190819003602001902060010154156108b35760405162461bcd60e51b815260206004820152601760248201527f4744494420616c7265616479207265676973746572656400000000000000000060448201526064016100fa565b60405180610100016040528086815260200184815260200183815260200182815260200160008152602001428152602001428152602001336001600160a01b031681525060008660405161090791906111ad565b90815260200160405180910390206000820151816000019080519060200190610931929190610ae0565b50602082810151805161094a9260018501920190610a2a565b5060408201518051610966916002840191602090910190610a87565b5060608201518051610982916003840191602090910190610a87565b506080820151600482015560a0820151600582015560c0820151600682015560e090910151600790910180546001600160a01b0319166001600160a01b039092169190911790556040516109d79086906111ad565b60405180910390207f806e4ee8c00b557ccd586b352eb0a8bafafc0f1cf5fd38e401747da5905eceed8585858542336000604051610a1b9796959493929190611278565b60405180910390a25050505050565b828054828255906000526020600020908101928215610a77579160200282015b82811115610a775782518051610a67918491602090910190610ae0565b5091602001919060010190610a4a565b50610a83929150610b60565b5090565b828054828255906000526020600020908101928215610ad4579160200282015b82811115610ad45782518051610ac4918491602090910190610ae0565b5091602001919060010190610aa7565b50610a83929150610b7d565b828054610aec906111c9565b90600052602060002090601f016020900481019282610b0e5760008555610b54565b82601f10610b2757805160ff1916838001178555610b54565b82800160010185558215610b54579182015b82811115610b54578251825591602001919060010190610b39565b50610a83929150610b9a565b80821115610a83576000610b748282610baf565b50600101610b60565b80821115610a83576000610b918282610baf565b50600101610b7d565b5b80821115610a835760008155600101610b9b565b508054610bbb906111c9565b6000825580601f10610bcb575050565b601f016020900490600052602060002090810190610be99190610b9a565b50565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610c2b57610c2b610bec565b604052919050565b600067ffffffffffffffff831115610c4d57610c4d610bec565b610c60601f8401601f1916602001610c02565b9050828152838383011115610c7457600080fd5b828260208301376000602084830101529392505050565b600082601f830112610c9c57600080fd5b610cab83833560208501610c33565b9392505050565b600060208284031215610cc457600080fd5b813567ffffffffffffffff811115610cdb57600080fd5b610ce784828501610c8b565b949350505050565b60005b83811015610d0a578181015183820152602001610cf2565b83811115610d19576000848401525b50505050565b60008151808452610d37816020860160208601610cef565b601f01601f19169290920160200192915050565b600082825180855260208086019550808260051b84010181860160005b84811015610d9657601f19868403018952610d84838351610d1f565b98840198925090830190600101610d68565b5090979650505050505050565b600082825180855260208086019550808260051b84010181860160005b84811015610d9657601f19868403018952610ddc838351610d1f565b98840198925090830190600101610dc0565b6000610100808352610e028184018c610d1f565b90508281036020840152610e16818b610d4b565b90508281036040840152610e2a818a610da3565b90508281036060840152610e3e8189610da3565b6080840197909752505060a081019390935260c08301919091526001600160a01b031660e090910152949350505050565b600067ffffffffffffffff821115610e8957610e89610bec565b5060051b60200190565b600082601f830112610ea457600080fd5b81356020610eb9610eb483610e6f565b610c02565b82815260059290921b84018101918181019086841115610ed857600080fd5b8286015b84811015610f2d57803567ffffffffffffffff811115610efc5760008081fd5b8701603f81018913610f0e5760008081fd5b610f1f898683013560408401610c33565b845250918301918301610edc565b509695505050505050565b600082601f830112610f4957600080fd5b81356020610f59610eb483610e6f565b82815260059290921b84018101918181019086841115610f7857600080fd5b8286015b84811015610f2d57803567ffffffffffffffff811115610f9c5760008081fd5b610faa8986838b0101610c8b565b845250918301918301610f7c565b600080600080600060a08688031215610fd057600080fd5b853567ffffffffffffffff80821115","610fe857600080fd5b610ff489838a01610c8b565b9650602088013591508082111561100a57600080fd5b61101689838a01610e93565b9550604088013591508082111561102c57600080fd5b61103889838a01610f38565b9450606088013591508082111561104e57600080fd5b5061105b88828901610f38565b95989497509295608001359392505050565b60a08152600061108060a0830188610d1f565b602083019690965250604081019390935260608301919091526001600160a01b0316608090910152919050565b600080600080600060a086880312156110c557600080fd5b853567ffffffffffffffff808211156110dd57600080fd5b6110e989838a01610c8b565b965060208801359150808211156110ff57600080fd5b61110b89838a01610c8b565b9550604088013591508082111561112157600080fd5b61112d89838a01610e93565b9450606088013591508082111561114357600080fd5b61114f89838a01610f38565b9350608088013591508082111561116557600080fd5b5061117288828901610f38565b9150509295509295909350565b602080825260149082015273474449442063616e6e6f7420626520656d70747960601b604082015260600190565b600082516111bf818460208701610cef565b9190910192915050565b600181811c908216806111dd57607f821691505b602082108114156111fe57634e487b7160e01b600052602260045260246000fd5b50919050565b6000821982111561122557634e487b7160e01b600052601160045260246000fd5b500190565b60a08152600061123d60a0830188610d4b565b828103602084015261124f8188610da3565b905082810360408401526112638187610da3565b60608401959095525050608001529392505050565b60e08152600061128b60e083018a610d1f565b828103602084015261129d818a610d4b565b905082810360408401526112b18189610da3565b905082810360608401526112c58188610da3565b608084019690965250506001600160a01b039290921660a083015260c09091015294935050505056fea2646970667358221220a2dffc28906456ce8561a19b452a38ed5b8375989f5ea5a05b8c43682458dfa164736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b506112e2806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80635548e36514610051578063661456bb1461007e578063dea36cd4146100a5578063f7f3ee2e146100ba575b600080fd5b61006461005f366004610cbb565b6100cd565b604051610075959493929190610d54565b60405180910390f35b61009161008c366004610cbb565b610198565b604051610075989796959493929190610dec565b6100b86100b3366004610fb6565b6105db565b005b6100b86100c8366004611088565b610837565b80516020818301810180516000825292820191909301209152805481906100f39061113d565b80601f016020809104026020016040519081016040528092919081815260200182805461011f9061113d565b801561016c5780601f106101415761010080835404028352916020019161016c565b820191906000526020600020905b81548152906001019060200180831161014f57829003601f168201915b50505050600483015460058401546006850154600790950154939491939092506001600160a01b031685565b60608060608060008060008060008951116101cf57604051636381e58960e11b81526004016101c690611178565b60405180910390fd5b6000808a6040516101e091906111a6565b90815260200160405180910390206040518061010001604052908160008201805461020a9061113d565b80601f01602080910402602001604051908101604052809291908181526020018280546102369061113d565b80156102835780601f1061025857610100808354040283529160200191610283565b820191906000526020600020905b81548152906001019060200180831161026657829003601f168201915b5050505050815260200160018201805480602002602001604051908101604052809291908181526020016000905b8282101561035d5783829060005260206000200180546102d09061113d565b80601f01602080910402602001604051908101604052809291908181526020018280546102fc9061113d565b80156103495780601f1061031e57610100808354040283529160200191610349565b820191906000526020600020905b81548152906001019060200180831161032c57829003601f168201915b5050505050815260200190600101906102b1565b50505050815260200160028201805480602002602001604051908101604052809291908181526020016000905b828210156104365783829060005260206000200180546103a99061113d565b80601f01602080910402602001604051908101604052809291908181526020018280546103d59061113d565b80156104225780601f106103f757610100808354040283529160200191610422565b820191906000526020600020905b81548152906001019060200180831161040557829003601f168201915b50505050508152602001906001019061038a565b50505050815260200160038201805480602002602001604051908101604052809291908181526020016000905b8282101561050f5783829060005260206000200180546104829061113d565b80601f01602080910402602001604051908101604052809291908181526020018280546104ae9061113d565b80156104fb5780601f106104d0576101008083540402835291602001916104fb565b820191906000526020600020905b8154815290600101906020018083116104de57829003601f168201915b505050505081526020019060010190610463565b50505090825250600482015460208083019190915260058301546040830152600683015460608301526007909201546001600160a01b03166080909101528101515190915061059757604051636381e58960e11b815260206004820152601360248201527211d11251081b9bdd081c9959da5cdd195c9959606a1b60448201526064016101c6565b806000015181602001518260400151836060015184608001518560a001518660c001518760e001519850985098509850985098509850985050919395975091939597565b60008551116105fd57604051636381e58960e11b81526004016101c690611178565b600083511161064f57604051636381e58960e11b815260206004820152601a60248201527f5075626c6963206b65792063616e6e6f7420626520656d70747900000000000060448201526064016101c6565b60008560405161065f91906111a6565b90815260405190819003602001902060010154156106c057604051636381e58960e11b815260206004820152601760248201527f4744494420616c7265616479207265676973746572656400000000000000000060448201526064016101c6565b60405180610100016040528086815260200184815260200183815260200182815260200160008152602001428152602001428152602001336001600160a01b031681525060008660405161071491906111a6565b9081526020016040518091039020600082015181600001908051906020019061073e929190610a33565b5060208281015180516107579260018501920190610ab7565b5060408201518051610773916002840191602090910190610b10565b506060820151805161078f916003840191602090910190610b10565b506080820151600482015560a0820151600582015560c0820151600682015560e090910151600790910180546001600160a01b0319166001600160a01b039092169190911790556040516107e49086906111a6565b60405180910390207f89184cbdf6aead5976b642cf7cccb0f4bf33e5536d733c8e50c57819a6fc5669858585854233600060405161082897969594939291906111c2565b60405180910390a25050505050565b600085511161085957604051636381e58960e11b81526004016101c690611178565b6000808660405161086a91906111a6565b90815260405190819003602001902060018101549091506108c457604051636381e58960e11b815260206004820152601360248201527211d11251081b9bdd081c9959da5cdd195c9959606a1b60448201526064016101c6565b8181600401541461091857604051636381e58960e11b815260206004820152601760248201527f496e76616c69642073657175656e6365206e756d62657200000000000000000060448201526064016101c6565b60078101546001600160a01b0316331461096d57604051636381e58960e11b81526020600482015260156024820152744f6e6c79206f776e65722063616e2075706461746560581b60448201526064016101c6565b84516109829060018301906020880190610ab7565b5083516109989060028301906020870190610b10565b5082516109ae9060038301906020860190610b10565b5042816006018190555060018160040160008282546109cd9190611238565b90915550506040516109e09087906111a6565b60405180910390207f12576e67dc6278b73bb6f604a73a49a5b2df184816c2f183bf29fc4160c344a2868686428660040154604051610a2395949392919061125e565b60405180910390a2505050505050565b828054610a3f9061113d565b90600052602060002090601f016020900481019282610a615760008555610aa7565b82601f10610a7a57805160ff1916838001178555610aa7565b82800160010185558215610aa7579182015b82811115610aa7578251825591602001919060010190610a8c565b50610ab3929150610b69565b5090565b828054828255906000526020600020908101928215610b04579160200282015b82811115610b045782518051610af4918491602090910190610a33565b5091602001919060010190610ad7565b50610ab3929150610b7e565b828054828255906000526020600020908101928215610b5d579160200282015b82811115610b5d5782518051610b4d918491602090910190610a33565b5091602001919060010190610b30565b50610ab3929150610b9b565b5b80821115610ab35760008155600101610b6a565b80821115610ab3576000610b928282610bb8565b50600101610b7e565b80821115610ab3576000610baf8282610bb8565b50600101610b9b565b508054610bc49061113d565b6000825580601f10610bd4575050565b601f016020900490600052602060002090810190610bf29190610b69565b50565b63b95aa35560e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610c3457610c34610bf5565b604052919050565b600067ffffffffffffffff831115610c5657610c56610bf5565b610c69601f8401601f1916602001610c0b565b9050828152838383011115610c7d57600080fd5b828260208301376000602084830101529392505050565b600082601f830112610ca557600080fd5b610cb483833560208501610c3c565b9392505050565b600060208284031215610ccd57600080fd5b813567ffffffffffffffff811115610ce457600080fd5b610cf084828501610c94565b949350505050565b60005b83811015610d13578181015183820152602001610cfb565b83811115610d22576000848401525b50505050565b60008151808452610d40816020860160208601610cf8565b601f01601f19169290920160200192915050565b60a081526000610d6760a0830188610d28565b602083019690965250604081019390935260608301919091526001600160a01b0316608090910152919050565b600082825180855260208086019550808260051b84010181860160005b84811015610ddf57601f19868403018952610dcd838351610d28565b98840198925090830190600101610db1565b5090979650505050505050565b6000610100808352610e008184018c610d28565b90508281036020840152610e14818b610d94565b90508281036040840152610e28818a610d94565b90508281036060840152610e3c8189610d94565b6080840197909752505060a081019390935260c08301919091526001600160a01b031660e090910152949350505050565b600067ffffffffffffffff821115610e8757610e87610bf5565b5060051b60200190565b600082601f830112610ea257600080fd5b81356020610eb7610eb283610e6d565b610c0b565b82815260059290921b84018101918181019086841115610ed657600080fd5b8286015b84811015610f2b57803567ffffffffffffffff811115610efa5760008081fd5b8701603f81018913610f0c5760008081fd5b610f1d898683013560408401610c3c565b845250918301918301610eda565b509695505050505050565b600082601f830112610f4757600080fd5b81356020610f57610eb283610e6d565b82815260059290921b84018101918181019086841115610f7657600080fd5b8286015b84811015610f2b57803567ffffffffffffffff811115610f9a5760008081fd5b610fa88986838b0101610c94565b845250918301918301610f7a565b600080600080600060a08688031215610fce57600080fd5b853567ffffffffffffffff80821115610f","e657600080fd5b610ff289838a01610c94565b9650602088013591508082111561100857600080fd5b61101489838a01610c94565b9550604088013591508082111561102a57600080fd5b61103689838a01610e91565b9450606088013591508082111561104c57600080fd5b61105889838a01610f36565b9350608088013591508082111561106e57600080fd5b5061107b88828901610f36565b9150509295509295909350565b600080600080600060a086880312156110a057600080fd5b853567ffffffffffffffff808211156110b857600080fd5b6110c489838a01610c94565b965060208801359150808211156110da57600080fd5b6110e689838a01610e91565b955060408801359150808211156110fc57600080fd5b61110889838a01610f36565b9450606088013591508082111561111e57600080fd5b5061112b88828901610f36565b95989497509295608001359392505050565b600181811c9082168061115157607f821691505b602082108114156111725763b95aa35560e01b600052602260045260246000fd5b50919050565b602080825260149082015273474449442063616e6e6f7420626520656d70747960601b604082015260600190565b600082516111b8818460208701610cf8565b9190910192915050565b60e0815260006111d560e083018a610d28565b82810360208401526111e7818a610d94565b905082810360408401526111fb8189610d94565b9050828103606084015261120f8188610d94565b608084019690965250506001600160a01b039290921660a083015260c090910152949350505050565b600082198211156112595763b95aa35560e01b600052601160045260246000fd5b500190565b60a08152600061127160a0830188610d94565b82810360208401526112838188610d94565b905082810360408401526112978187610d94565b6060840195909552505060800152939250505056fea264697066735822122041935d5fa61395984a895c711cc2fbfaf14458dc8e4eba7f8b5e08fd3e6a0ef764736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"bytes[]\",\"name\":\"publicKeys\",\"type\":\"bytes[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"didList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"}],\"name\":\"GDIDDocumentUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"did\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"bytes[]\",\"name\":\"pkList\",\"type\":\"bytes[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"didList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"}],\"name\":\"GDIDRegistered\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"name\":\"gdidDocuments\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"created\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"updated\",\"type\":\"uint256\"},{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"selector\":[2965895710,1430840165],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":0,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"}],\"name\":\"getGDIDDocument\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"gdid_\",\"type\":\"string\"},{\"internalType\":\"bytes[]\",\"name\":\"publicKey\",\"type\":\"bytes[]\"},{\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"didList\",\"type\":\"string[]\"},{\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"created\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"updated\",\"type\":\"uint256\"},{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"selector\":[274710496,1712608955],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"gdid_\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"did_\",\"type\":\"string\"},{\"internalType\":\"bytes[]\",\"name\":\"pkList_\",\"type\":\"bytes[]\"},{\"internalType\":\"string[]\",\"name\":\"serverList_\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"didLists_\",\"type\":\"string[]\"}],\"name\":\"registerGDID\",\"outputs\":[],\"selector\":[3587839500,3735252180],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"gdid_\",\"type\":\"string\"},{\"internalType\":\"bytes[]\",\"name\":\"publicKeys_\",\"type\":\"bytes[]\"},{\"internalType\":\"string[]\",\"name\":\"serviceList_\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"didList_\",\"type\":\"string[]\"},{\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"}],\"name\":\"updateGDIDDocument\",\"outputs\":[],\"selector\":[1190578193,4159958574],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GDIDDOCUMENTS = "gdidDocuments";

    public static final String FUNC_GETGDIDDOCUMENT = "getGDIDDocument";

    public static final String FUNC_REGISTERGDID = "registerGDID";

    public static final String FUNC_UPDATEGDIDDOCUMENT = "updateGDIDDocument";

    public static final Event GDIDDOCUMENTUPDATED_EVENT = new Event("GDIDDocumentUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event GDIDREGISTERED_EVENT = new Event("GDIDRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    protected GDIDRegistry(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public List<GDIDDocumentUpdatedEventResponse> getGDIDDocumentUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(GDIDDOCUMENTUPDATED_EVENT, transactionReceipt);
        ArrayList<GDIDDocumentUpdatedEventResponse> responses = new ArrayList<GDIDDocumentUpdatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            GDIDDocumentUpdatedEventResponse typedResponse = new GDIDDocumentUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.gdid = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.publicKeys = (List<byte[]>) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.serviceList = (List<String>) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.didList = (List<String>) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.seq = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeGDIDDocumentUpdatedEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(GDIDDOCUMENTUPDATED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeGDIDDocumentUpdatedEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(GDIDDOCUMENTUPDATED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public List<GDIDRegisteredEventResponse> getGDIDRegisteredEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(GDIDREGISTERED_EVENT, transactionReceipt);
        ArrayList<GDIDRegisteredEventResponse> responses = new ArrayList<GDIDRegisteredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            GDIDRegisteredEventResponse typedResponse = new GDIDRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.gdid = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.did = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.pkList = (List<byte[]>) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.serviceList = (List<String>) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.didList = (List<String>) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.owner = (String) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.seq = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeGDIDRegisteredEvent(BigInteger fromBlock, BigInteger toBlock,
            List<String> otherTopics, EventSubCallback callback) {
        String topic0 = eventEncoder.encode(GDIDREGISTERED_EVENT);
        subscribeEvent(topic0,otherTopics,fromBlock,toBlock,callback);
    }

    public void subscribeGDIDRegisteredEvent(EventSubCallback callback) {
        String topic0 = eventEncoder.encode(GDIDREGISTERED_EVENT);
        subscribeEvent(topic0,callback);
    }

    public Tuple5<String, BigInteger, BigInteger, BigInteger, String> gdidDocuments(String param0)
            throws ContractException {
        final Function function = new Function(FUNC_GDIDDOCUMENTS, 
                Arrays.<Type>asList(new Utf8String(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<String, BigInteger, BigInteger, BigInteger, String>(
                (String) results.get(0).getValue(), 
                (BigInteger) results.get(1).getValue(), 
                (BigInteger) results.get(2).getValue(), 
                (BigInteger) results.get(3).getValue(), 
                (String) results.get(4).getValue());
    }

    public Function getMethodGdidDocumentsRawFunction(String param0) throws ContractException {
        final Function function = new Function(FUNC_GDIDDOCUMENTS, 
                Arrays.<Type>asList(new Utf8String(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        return function;
    }

    public Tuple8<String, List<byte[]>, List<String>, List<String>, BigInteger, BigInteger, BigInteger, String> getGDIDDocument(
            String gdid) throws ContractException {
        final Function function = new Function(FUNC_GETGDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(gdid)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple8<String, List<byte[]>, List<String>, List<String>, BigInteger, BigInteger, BigInteger, String>(
                (String) results.get(0).getValue(), 
                convertToNative((List<DynamicBytes>) results.get(1).getValue()), 
                convertToNative((List<Utf8String>) results.get(2).getValue()), 
                convertToNative((List<Utf8String>) results.get(3).getValue()), 
                (BigInteger) results.get(4).getValue(), 
                (BigInteger) results.get(5).getValue(), 
                (BigInteger) results.get(6).getValue(), 
                (String) results.get(7).getValue());
    }

    public Function getMethodGetGDIDDocumentRawFunction(String gdid) throws ContractException {
        final Function function = new Function(FUNC_GETGDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(gdid)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
        return function;
    }

    public TransactionReceipt registerGDID(String gdid_, String did_, List<byte[]> pkList_,
            List<String> serverList_, List<String> didLists_) {
        final Function function = new Function(
                FUNC_REGISTERGDID, 
                Arrays.<Type>asList(new Utf8String(gdid_),
                new Utf8String(did_),
                new DynamicArray<DynamicBytes>(
                        DynamicBytes.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pkList_, DynamicBytes.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serverList_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(didLists_, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodRegisterGDIDRawFunction(String gdid_, String did_,
            List<byte[]> pkList_, List<String> serverList_, List<String> didLists_) throws
            ContractException {
        final Function function = new Function(FUNC_REGISTERGDID, 
                Arrays.<Type>asList(new Utf8String(gdid_),
                new Utf8String(did_),
                new DynamicArray<DynamicBytes>(
                        DynamicBytes.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pkList_, DynamicBytes.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serverList_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(didLists_, Utf8String.class))),
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForRegisterGDID(String gdid_, String did_,
            List<byte[]> pkList_, List<String> serverList_, List<String> didLists_) {
        final Function function = new Function(
                FUNC_REGISTERGDID, 
                Arrays.<Type>asList(new Utf8String(gdid_),
                new Utf8String(did_),
                new DynamicArray<DynamicBytes>(
                        DynamicBytes.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pkList_, DynamicBytes.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serverList_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(didLists_, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String registerGDID(String gdid_, String did_, List<byte[]> pkList_,
            List<String> serverList_, List<String> didLists_, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_REGISTERGDID, 
                Arrays.<Type>asList(new Utf8String(gdid_),
                new Utf8String(did_),
                new DynamicArray<DynamicBytes>(
                        DynamicBytes.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(pkList_, DynamicBytes.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serverList_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(didLists_, Utf8String.class))),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple5<String, String, List<byte[]>, List<String>, List<String>> getRegisterGDIDInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_REGISTERGDID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple5<String, String, List<byte[]>, List<String>, List<String>>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                convertToNative((List<DynamicBytes>) results.get(2).getValue()), 
                convertToNative((List<Utf8String>) results.get(3).getValue()), 
                convertToNative((List<Utf8String>) results.get(4).getValue())
                );
    }

    public TransactionReceipt updateGDIDDocument(String gdid_, List<byte[]> publicKeys_,
            List<String> serviceList_, List<String> didList_, BigInteger seq) {
        final Function function = new Function(
                FUNC_UPDATEGDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(gdid_),
                new DynamicArray<DynamicBytes>(
                        DynamicBytes.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys_, DynamicBytes.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(didList_, Utf8String.class)),
                new Uint256(seq)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodUpdateGDIDDocumentRawFunction(String gdid_, List<byte[]> publicKeys_,
            List<String> serviceList_, List<String> didList_, BigInteger seq) throws
            ContractException {
        final Function function = new Function(FUNC_UPDATEGDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(gdid_),
                new DynamicArray<DynamicBytes>(
                        DynamicBytes.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys_, DynamicBytes.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(didList_, Utf8String.class)),
                new Uint256(seq)),
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForUpdateGDIDDocument(String gdid_, List<byte[]> publicKeys_,
            List<String> serviceList_, List<String> didList_, BigInteger seq) {
        final Function function = new Function(
                FUNC_UPDATEGDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(gdid_),
                new DynamicArray<DynamicBytes>(
                        DynamicBytes.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys_, DynamicBytes.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(didList_, Utf8String.class)),
                new Uint256(seq)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String updateGDIDDocument(String gdid_, List<byte[]> publicKeys_,
            List<String> serviceList_, List<String> didList_, BigInteger seq,
            TransactionCallback callback) {
        final Function function = new Function(
                FUNC_UPDATEGDIDDOCUMENT, 
                Arrays.<Type>asList(new Utf8String(gdid_),
                new DynamicArray<DynamicBytes>(
                        DynamicBytes.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(publicKeys_, DynamicBytes.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(serviceList_, Utf8String.class)),
                new DynamicArray<Utf8String>(
                        Utf8String.class,
                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(didList_, Utf8String.class)),
                new Uint256(seq)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple5<String, List<byte[]>, List<String>, List<String>, BigInteger> getUpdateGDIDDocumentInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UPDATEGDIDDOCUMENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple5<String, List<byte[]>, List<String>, List<String>, BigInteger>(

                (String) results.get(0).getValue(), 
                convertToNative((List<DynamicBytes>) results.get(1).getValue()), 
                convertToNative((List<Utf8String>) results.get(2).getValue()), 
                convertToNative((List<Utf8String>) results.get(3).getValue()), 
                (BigInteger) results.get(4).getValue()
                );
    }

    public static GDIDRegistry load(String contractAddress, Client client,
            CryptoKeyPair credential) {
        return new GDIDRegistry(contractAddress, client, credential);
    }

    public static GDIDRegistry deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(GDIDRegistry.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }

    public static class GDIDDocumentUpdatedEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] gdid;

        public List<byte[]> publicKeys;

        public List<String> serviceList;

        public List<String> didList;

        public BigInteger timestamp;

        public BigInteger seq;
    }

    public static class GDIDRegisteredEventResponse {
        public TransactionReceipt.Logs log;

        public byte[] gdid;

        public String did;

        public List<byte[]> pkList;

        public List<String> serviceList;

        public List<String> didList;

        public BigInteger timestamp;

        public String owner;

        public BigInteger seq;
    }
}
