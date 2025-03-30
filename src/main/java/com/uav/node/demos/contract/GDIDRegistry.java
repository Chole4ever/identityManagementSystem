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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
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
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b506112eb806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063105fbfe01461005157806346f6c811146100815780635a15209714610096578063b0c7fa1e146100a9575b600080fd5b61006461005f366004610c89565b6100cd565b604051610078989796959493929190610dc5565b60405180910390f35b61009461008f366004610f8f565b61050e565b005b6100946100a4366004611044565b6106f3565b6100bc6100b7366004610c89565b610936565b6040516100789594939291906110f1565b60608060608060008060008060008951116101035760405162461bcd60e51b81526004016100fa90611131565b60405180910390fd5b6000808a604051610114919061115f565b90815260200160405180910390206040518061010001604052908160008201805461013e9061117b565b80601f016020809104026020016040519081016040528092919081815260200182805461016a9061117b565b80156101b75780601f1061018c576101008083540402835291602001916101b7565b820191906000526020600020905b81548152906001019060200180831161019a57829003601f168201915b5050505050815260200160018201805480602002602001604051908101604052809291908181526020016000905b828210156102915783829060005260206000200180546102049061117b565b80601f01602080910402602001604051908101604052809291908181526020018280546102309061117b565b801561027d5780601f106102525761010080835404028352916020019161027d565b820191906000526020600020905b81548152906001019060200180831161026057829003601f168201915b5050505050815260200190600101906101e5565b50505050815260200160028201805480602002602001604051908101604052809291908181526020016000905b8282101561036a5783829060005260206000200180546102dd9061117b565b80601f01602080910402602001604051908101604052809291908181526020018280546103099061117b565b80156103565780601f1061032b57610100808354040283529160200191610356565b820191906000526020600020905b81548152906001019060200180831161033957829003601f168201915b5050505050815260200190600101906102be565b50505050815260200160038201805480602002602001604051908101604052809291908181526020016000905b828210156104435783829060005260206000200180546103b69061117b565b80601f01602080910402602001604051908101604052809291908181526020018280546103e29061117b565b801561042f5780601f106104045761010080835404028352916020019161042f565b820191906000526020600020905b81548152906001019060200180831161041257829003601f168201915b505050505081526020019060010190610397565b50505090825250600482015460208083019190915260058301546040830152600683015460608301526007909201546001600160a01b0316608090910152810151519091506104ca5760405162461bcd60e51b815260206004820152601360248201527211d11251081b9bdd081c9959da5cdd195c9959606a1b60448201526064016100fa565b806000015181602001518260400151836060015184608001518560a001518660c001518760e001519850985098509850985098509850985050919395975091939597565b600085511161052f5760405162461bcd60e51b81526004016100fa90611131565b60008086604051610540919061115f565b90815260405190819003602001902060018101549091506105995760405162461bcd60e51b815260206004820152601360248201527211d11251081b9bdd081c9959da5cdd195c9959606a1b60448201526064016100fa565b818160040154146105ec5760405162461bcd60e51b815260206004820152601760248201527f496e76616c69642073657175656e6365206e756d62657200000000000000000060448201526064016100fa565b60078101546001600160a01b031633146106405760405162461bcd60e51b81526020600482015260156024820152744f6e6c79206f776e65722063616e2075706461746560581b60448201526064016100fa565b84516106559060018301906020880190610a01565b50835161066b9060028301906020870190610a5e565b5082516106819060038301906020860190610a5e565b5042816006018190555060018160040160008282546106a091906111b6565b909155505060048101546040517fc98a85659fd09f1abc684fd3919bfa43dae7d88bb53c2abc23b180fab43d461d916106e39189918991899189914291906111dc565b60405180910390a1505050505050565b60008451116107145760405162461bcd60e51b81526004016100fa90611131565b60008351116107655760405162461bcd60e51b815260206004820152601a60248201527f5075626c6963206b65792063616e6e6f7420626520656d70747900000000000060448201526064016100fa565b600084604051610775919061115f565b90815260405190819003602001902060010154156107d55760405162461bcd60e51b815260206004820152601760248201527f4744494420616c7265616479207265676973746572656400000000000000000060448201526064016100fa565b60405180610100016040528085815260200184815260200183815260200182815260200160008152602001428152602001428152602001336001600160a01b0316815250600085604051610829919061115f565b90815260200160405180910390206000820151816000019080519060200190610853929190610ab7565b50602082810151805161086c9260018501920190610a01565b5060408201518051610888916002840191602090910190610a5e565b50606082015180516108a4916003840191602090910190610a5e565b506080820151600482015560a0820151600582015560c0820151600682015560e090910151600790910180546001600160a01b0319166001600160a01b039092169190911790556040517fae158db889a60a7902d3c2c44d26e254fcfd7c49645ad5d6a1d9cd192fe17f99906109289086908690869086904290339060009061123f565b60405180910390a150505050565b805160208183018101805160008252928201919093012091528054819061095c9061117b565b80601f01602080910402602001604051908101604052809291908181526020018280546109889061117b565b80156109d55780601f106109aa576101008083540402835291602001916109d5565b820191906000526020600020905b8154815290600101906020018083116109b857829003601f168201915b50505050600483015460058401546006850154600790950154939491939092506001600160a01b031685565b828054828255906000526020600020908101928215610a4e579160200282015b82811115610a4e5782518051610a3e918491602090910190610ab7565b5091602001919060010190610a21565b50610a5a929150610b37565b5090565b828054828255906000526020600020908101928215610aab579160200282015b82811115610aab5782518051610a9b918491602090910190610ab7565b5091602001919060010190610a7e565b50610a5a929150610b54565b828054610ac39061117b565b90600052602060002090601f016020900481019282610ae55760008555610b2b565b82601f10610afe57805160ff1916838001178555610b2b565b82800160010185558215610b2b579182015b82811115610b2b578251825591602001919060010190610b10565b50610a5a929150610b71565b80821115610a5a576000610b4b8282610b86565b50600101610b37565b80821115610a5a576000610b688282610b86565b50600101610b54565b5b80821115610a5a5760008155600101610b72565b508054610b929061117b565b6000825580601f10610ba2575050565b601f016020900490600052602060002090810190610bc09190610b71565b50565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610c0257610c02610bc3565b604052919050565b600067ffffffffffffffff831115610c2457610c24610bc3565b610c37601f8401601f1916602001610bd9565b9050828152838383011115610c4b57600080fd5b828260208301376000602084830101529392505050565b600082601f830112610c7357600080fd5b610c8283833560208501610c0a565b9392505050565b600060208284031215610c9b57600080fd5b813567ffffffffffffffff811115610cb257600080fd5b610cbe84828501610c62565b949350505050565b60005b83811015610ce1578181015183820152602001610cc9565b83811115610cf0576000848401525b50505050565b60008151808452610d0e816020860160208601610cc6565b601f01601f19169290920160200192915050565b600082825180855260208086019550808260051b84010181860160005b84811015610d6d57601f19868403018952610d5b838351610cf6565b98840198925090830190600101610d3f565b5090979650505050505050565b600082825180855260208086019550808260051b84010181860160005b84811015610d6d57601f19868403018952610db3838351610cf6565b98840198925090830190600101610d97565b6000610100808352610dd98184018c610cf6565b90508281036020840152610ded818b610d22565b90508281036040840152610e01818a610d7a565b90508281036060840152610e158189610d7a565b6080840197909752505060a081019390935260c08301919091526001600160a01b031660e090910152949350505050565b600067ffffffffffffffff821115610e6057610e60610bc3565b5060051b60200190565b600082601f830112610e7b57600080fd5b81356020610e90610e8b83610e46565b610bd9565b82815260059290921b84018101918181019086841115610eaf57600080fd5b8286015b84811015610f0457803567ffffffffffffffff811115610ed35760008081fd5b8701603f81018913610ee55760008081fd5b610ef6898683013560408401610c0a565b845250918301918301610eb3565b509695505050505050565b600082601f830112610f2057600080fd5b81356020610f30610e8b83610e46565b82815260059290921b84018101918181019086841115610f4f57600080fd5b8286015b84811015610f0457803567ffffffffffffffff811115610f735760008081fd5b610f818986838b0101610c62565b845250918301918301610f53565b600080600080600060a08688031215610fa757600080fd5b853567ffffffffffffffff80821115610fbf57600080fd5b610fcb89838a01610c62565b96506020880135915080821115610fe157600080","fd5b610fed89838a01610e6a565b9550604088013591508082111561100357600080fd5b61100f89838a01610f0f565b9450606088013591508082111561102557600080fd5b5061103288828901610f0f565b95989497509295608001359392505050565b6000806000806080858703121561105a57600080fd5b843567ffffffffffffffff8082111561107257600080fd5b61107e88838901610c62565b9550602087013591508082111561109457600080fd5b6110a088838901610e6a565b945060408701359150808211156110b657600080fd5b6110c288838901610f0f565b935060608701359150808211156110d857600080fd5b506110e587828801610f0f565b91505092959194509250565b60a08152600061110460a0830188610cf6565b602083019690965250604081019390935260608301919091526001600160a01b0316608090910152919050565b602080825260149082015273474449442063616e6e6f7420626520656d70747960601b604082015260600190565b60008251611171818460208701610cc6565b9190910192915050565b600181811c9082168061118f57607f821691505b602082108114156111b057634e487b7160e01b600052602260045260246000fd5b50919050565b600082198211156111d757634e487b7160e01b600052601160045260246000fd5b500190565b60c0815260006111ef60c0830189610cf6565b82810360208401526112018189610d22565b905082810360408401526112158188610d7a565b905082810360608401526112298187610d7a565b6080840195909552505060a00152949350505050565b60e08152600061125260e083018a610cf6565b8281036020840152611264818a610d22565b905082810360408401526112788189610d7a565b9050828103606084015261128c8188610d7a565b608084019690965250506001600160a01b039290921660a083015260c09091015294935050505056fea264697066735822122062818214b848d7afa16b699a5cd82672351671b34d0982e58180da597677482564736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b506112a9806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80635548e36514610051578063661456bb1461007e578063712b89df146100a5578063f7f3ee2e146100ba575b600080fd5b61006461005f366004610c92565b6100cd565b604051610075959493929190610d2b565b60405180910390f35b61009161008c366004610c92565b610198565b604051610075989796959493929190610dc3565b6100b86100b3366004610f8d565b6105db565b005b6100b86100c836600461103a565b610821565b80516020818301810180516000825292820191909301209152805481906100f3906110ef565b80601f016020809104026020016040519081016040528092919081815260200182805461011f906110ef565b801561016c5780601f106101415761010080835404028352916020019161016c565b820191906000526020600020905b81548152906001019060200180831161014f57829003601f168201915b50505050600483015460058401546006850154600790950154939491939092506001600160a01b031685565b60608060608060008060008060008951116101cf57604051636381e58960e11b81526004016101c69061112a565b60405180910390fd5b6000808a6040516101e09190611158565b90815260200160405180910390206040518061010001604052908160008201805461020a906110ef565b80601f0160208091040260200160405190810160405280929190818152602001828054610236906110ef565b80156102835780601f1061025857610100808354040283529160200191610283565b820191906000526020600020905b81548152906001019060200180831161026657829003601f168201915b5050505050815260200160018201805480602002602001604051908101604052809291908181526020016000905b8282101561035d5783829060005260206000200180546102d0906110ef565b80601f01602080910402602001604051908101604052809291908181526020018280546102fc906110ef565b80156103495780601f1061031e57610100808354040283529160200191610349565b820191906000526020600020905b81548152906001019060200180831161032c57829003601f168201915b5050505050815260200190600101906102b1565b50505050815260200160028201805480602002602001604051908101604052809291908181526020016000905b828210156104365783829060005260206000200180546103a9906110ef565b80601f01602080910402602001604051908101604052809291908181526020018280546103d5906110ef565b80156104225780601f106103f757610100808354040283529160200191610422565b820191906000526020600020905b81548152906001019060200180831161040557829003601f168201915b50505050508152602001906001019061038a565b50505050815260200160038201805480602002602001604051908101604052809291908181526020016000905b8282101561050f578382906000526020600020018054610482906110ef565b80601f01602080910402602001604051908101604052809291908181526020018280546104ae906110ef565b80156104fb5780601f106104d0576101008083540402835291602001916104fb565b820191906000526020600020905b8154815290600101906020018083116104de57829003601f168201915b505050505081526020019060010190610463565b50505090825250600482015460208083019190915260058301546040830152600683015460608301526007909201546001600160a01b03166080909101528101515190915061059757604051636381e58960e11b815260206004820152601360248201527211d11251081b9bdd081c9959da5cdd195c9959606a1b60448201526064016101c6565b806000015181602001518260400151836060015184608001518560a001518660c001518760e001519850985098509850985098509850985050919395975091939597565b60008451116105fd57604051636381e58960e11b81526004016101c69061112a565b600083511161064f57604051636381e58960e11b815260206004820152601a60248201527f5075626c6963206b65792063616e6e6f7420626520656d70747900000000000060448201526064016101c6565b60008460405161065f9190611158565b90815260405190819003602001902060010154156106c057604051636381e58960e11b815260206004820152601760248201527f4744494420616c7265616479207265676973746572656400000000000000000060448201526064016101c6565b60405180610100016040528085815260200184815260200183815260200182815260200160008152602001428152602001428152602001336001600160a01b03168152506000856040516107149190611158565b9081526020016040518091039020600082015181600001908051906020019061073e929190610a0a565b5060208281015180516107579260018501920190610a8e565b5060408201518051610773916002840191602090910190610ae7565b506060820151805161078f916003840191602090910190610ae7565b506080820151600482015560a0820151600582015560c0820151600682015560e090910151600790910180546001600160a01b0319166001600160a01b039092169190911790556040517fb936ead9ffdfc2c50f44acfc88497cc57d13df186bf5d7a5909b5d253bf149c99061081390869086908690869042903390600090611174565b60405180910390a150505050565b600085511161084357604051636381e58960e11b81526004016101c69061112a565b600080866040516108549190611158565b90815260405190819003602001902060018101549091506108ae57604051636381e58960e11b815260206004820152601360248201527211d11251081b9bdd081c9959da5cdd195c9959606a1b60448201526064016101c6565b8181600401541461090257604051636381e58960e11b815260206004820152601760248201527f496e76616c69642073657175656e6365206e756d62657200000000000000000060448201526064016101c6565b60078101546001600160a01b0316331461095757604051636381e58960e11b81526020600482015260156024820152744f6e6c79206f776e65722063616e2075706461746560581b60448201526064016101c6565b845161096c9060018301906020880190610a8e565b5083516109829060028301906020870190610ae7565b5082516109989060038301906020860190610ae7565b5042816006018190555060018160040160008282546109b791906111ea565b909155505060048101546040517f12576e67dc6278b73bb6f604a73a49a5b2df184816c2f183bf29fc4160c344a2916109fa918991899189918991429190611210565b60405180910390a1505050505050565b828054610a16906110ef565b90600052602060002090601f016020900481019282610a385760008555610a7e565b82601f10610a5157805160ff1916838001178555610a7e565b82800160010185558215610a7e579182015b82811115610a7e578251825591602001919060010190610a63565b50610a8a929150610b40565b5090565b828054828255906000526020600020908101928215610adb579160200282015b82811115610adb5782518051610acb918491602090910190610a0a565b5091602001919060010190610aae565b50610a8a929150610b55565b828054828255906000526020600020908101928215610b34579160200282015b82811115610b345782518051610b24918491602090910190610a0a565b5091602001919060010190610b07565b50610a8a929150610b72565b5b80821115610a8a5760008155600101610b41565b80821115610a8a576000610b698282610b8f565b50600101610b55565b80821115610a8a576000610b868282610b8f565b50600101610b72565b508054610b9b906110ef565b6000825580601f10610bab575050565b601f016020900490600052602060002090810190610bc99190610b40565b50565b63b95aa35560e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff81118282101715610c0b57610c0b610bcc565b604052919050565b600067ffffffffffffffff831115610c2d57610c2d610bcc565b610c40601f8401601f1916602001610be2565b9050828152838383011115610c5457600080fd5b828260208301376000602084830101529392505050565b600082601f830112610c7c57600080fd5b610c8b83833560208501610c13565b9392505050565b600060208284031215610ca457600080fd5b813567ffffffffffffffff811115610cbb57600080fd5b610cc784828501610c6b565b949350505050565b60005b83811015610cea578181015183820152602001610cd2565b83811115610cf9576000848401525b50505050565b60008151808452610d17816020860160208601610ccf565b601f01601f19169290920160200192915050565b60a081526000610d3e60a0830188610cff565b602083019690965250604081019390935260608301919091526001600160a01b0316608090910152919050565b600082825180855260208086019550808260051b84010181860160005b84811015610db657601f19868403018952610da4838351610cff565b98840198925090830190600101610d88565b5090979650505050505050565b6000610100808352610dd78184018c610cff565b90508281036020840152610deb818b610d6b565b90508281036040840152610dff818a610d6b565b90508281036060840152610e138189610d6b565b6080840197909752505060a081019390935260c08301919091526001600160a01b031660e090910152949350505050565b600067ffffffffffffffff821115610e5e57610e5e610bcc565b5060051b60200190565b600082601f830112610e7957600080fd5b81356020610e8e610e8983610e44565b610be2565b82815260059290921b84018101918181019086841115610ead57600080fd5b8286015b84811015610f0257803567ffffffffffffffff811115610ed15760008081fd5b8701603f81018913610ee35760008081fd5b610ef4898683013560408401610c13565b845250918301918301610eb1565b509695505050505050565b600082601f830112610f1e57600080fd5b81356020610f2e610e8983610e44565b82815260059290921b84018101918181019086841115610f4d57600080fd5b8286015b84811015610f0257803567ffffffffffffffff811115610f715760008081fd5b610f7f8986838b0101610c6b565b845250918301918301610f51565b60008060008060808587031215610fa357600080fd5b843567ffffffffffffffff80821115610fbb57600080fd5b610fc788838901610c6b565b95506020870135915080821115610fdd57600080fd5b610f","e988838901610e68565b94506040870135915080821115610fff57600080fd5b61100b88838901610f0d565b9350606087013591508082111561102157600080fd5b5061102e87828801610f0d565b91505092959194509250565b600080600080600060a0868803121561105257600080fd5b853567ffffffffffffffff8082111561106a57600080fd5b61107689838a01610c6b565b9650602088013591508082111561108c57600080fd5b61109889838a01610e68565b955060408801359150808211156110ae57600080fd5b6110ba89838a01610f0d565b945060608801359150808211156110d057600080fd5b506110dd88828901610f0d565b95989497509295608001359392505050565b600181811c9082168061110357607f821691505b602082108114156111245763b95aa35560e01b600052602260045260246000fd5b50919050565b602080825260149082015273474449442063616e6e6f7420626520656d70747960601b604082015260600190565b6000825161116a818460208701610ccf565b9190910192915050565b60e08152600061118760e083018a610cff565b8281036020840152611199818a610d6b565b905082810360408401526111ad8189610d6b565b905082810360608401526111c18188610d6b565b608084019690965250506001600160a01b039290921660a083015260c090910152949350505050565b6000821982111561120b5763b95aa35560e01b600052601160045260246000fd5b500190565b60c08152600061122360c0830189610cff565b82810360208401526112358189610d6b565b905082810360408401526112498188610d6b565b9050828103606084015261125d8187610d6b565b6080840195909552505060a0015294935050505056fea2646970667358221220f007567a3c7e4bfda0a2d236d2e1cc48514cb0c5d7a50a0d50ea0286e84ad13864736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"bytes[]\",\"name\":\"publicKeys\",\"type\":\"bytes[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"didList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"}],\"name\":\"GDIDDocumentUpdated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"bytes[]\",\"name\":\"publicKeys\",\"type\":\"bytes[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"string[]\",\"name\":\"didList\",\"type\":\"string[]\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"created\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"}],\"name\":\"GDIDRegistered\",\"type\":\"event\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"name\":\"gdidDocuments\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"created\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"updated\",\"type\":\"uint256\"},{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"selector\":[2965895710,1430840165],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":3,\"slot\":0,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"gdid\",\"type\":\"string\"}],\"name\":\"getGDIDDocument\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"gdid_\",\"type\":\"string\"},{\"internalType\":\"bytes[]\",\"name\":\"publicKey\",\"type\":\"bytes[]\"},{\"internalType\":\"string[]\",\"name\":\"serviceList\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"didList\",\"type\":\"string[]\"},{\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"created\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"updated\",\"type\":\"uint256\"},{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"selector\":[274710496,1712608955],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"gdid_\",\"type\":\"string\"},{\"internalType\":\"bytes[]\",\"name\":\"pkList_\",\"type\":\"bytes[]\"},{\"internalType\":\"string[]\",\"name\":\"serverList_\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"didLists_\",\"type\":\"string[]\"}],\"name\":\"registerGDID\",\"outputs\":[],\"selector\":[1511334039,1898678751],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"gdid_\",\"type\":\"string\"},{\"internalType\":\"bytes[]\",\"name\":\"publicKeys_\",\"type\":\"bytes[]\"},{\"internalType\":\"string[]\",\"name\":\"serviceList_\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"didList_\",\"type\":\"string[]\"},{\"internalType\":\"uint256\",\"name\":\"seq\",\"type\":\"uint256\"}],\"name\":\"updateGDIDDocument\",\"outputs\":[],\"selector\":[1190578193,4159958574],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GDIDDOCUMENTS = "gdidDocuments";

    public static final String FUNC_GETGDIDDOCUMENT = "getGDIDDocument";

    public static final String FUNC_REGISTERGDID = "registerGDID";

    public static final String FUNC_UPDATEGDIDDOCUMENT = "updateGDIDDocument";

    public static final Event GDIDDOCUMENTUPDATED_EVENT = new Event("GDIDDocumentUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event GDIDREGISTERED_EVENT = new Event("GDIDRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
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
            typedResponse.gdid = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.publicKeys = (List<byte[]>) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.serviceList = (List<String>) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.didList = (List<String>) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.seq = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
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
            typedResponse.gdid = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.publicKeys = (List<byte[]>) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.serviceList = (List<String>) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.didList = (List<String>) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.created = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
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

    public TransactionReceipt registerGDID(String gdid_, List<byte[]> pkList_,
            List<String> serverList_, List<String> didLists_) {
        final Function function = new Function(
                FUNC_REGISTERGDID, 
                Arrays.<Type>asList(new Utf8String(gdid_),
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

    public Function getMethodRegisterGDIDRawFunction(String gdid_, List<byte[]> pkList_,
            List<String> serverList_, List<String> didLists_) throws ContractException {
        final Function function = new Function(FUNC_REGISTERGDID, 
                Arrays.<Type>asList(new Utf8String(gdid_),
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

    public String getSignedTransactionForRegisterGDID(String gdid_, List<byte[]> pkList_,
            List<String> serverList_, List<String> didLists_) {
        final Function function = new Function(
                FUNC_REGISTERGDID, 
                Arrays.<Type>asList(new Utf8String(gdid_),
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

    public String registerGDID(String gdid_, List<byte[]> pkList_, List<String> serverList_,
            List<String> didLists_, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_REGISTERGDID, 
                Arrays.<Type>asList(new Utf8String(gdid_),
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

    public Tuple4<String, List<byte[]>, List<String>, List<String>> getRegisterGDIDInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_REGISTERGDID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, List<byte[]>, List<String>, List<String>>(

                (String) results.get(0).getValue(), 
                convertToNative((List<DynamicBytes>) results.get(1).getValue()), 
                convertToNative((List<Utf8String>) results.get(2).getValue()), 
                convertToNative((List<Utf8String>) results.get(3).getValue())
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

        public String gdid;

        public List<byte[]> publicKeys;

        public List<String> serviceList;

        public List<String> didList;

        public BigInteger timestamp;

        public BigInteger seq;
    }

    public static class GDIDRegisteredEventResponse {
        public TransactionReceipt.Logs log;

        public String gdid;

        public List<byte[]> publicKeys;

        public List<String> serviceList;

        public List<String> didList;

        public BigInteger created;

        public String owner;

        public BigInteger seq;
    }
}
