package com.uav.node.demos.contract;

import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class HelloWorld extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b5060408051808201909152600d8082526c48656c6c6f2c20576f726c642160981b60209092019182526100459160009161004b565b5061011f565b828054610057906100e4565b90600052602060002090601f01602090048101928261007957600085556100bf565b82601f1061009257805160ff19168380011785556100bf565b828001600101855582156100bf579182015b828111156100bf5782518255916020019190600101906100a4565b506100cb9291506100cf565b5090565b5b808211156100cb57600081556001016100d0565b600181811c908216806100f857607f821691505b6020821081141561011957634e487b7160e01b600052602260045260246000fd5b50919050565b61033d8061012e6000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80634ed3885e1461003b5780636d4ce63c14610050575b600080fd5b61004e6100493660046101c6565b61006e565b005b610058610085565b6040516100659190610277565b60405180910390f35b8051610081906000906020840190610117565b5050565b606060008054610094906102cc565b80601f01602080910402602001604051908101604052809291908181526020018280546100c0906102cc565b801561010d5780601f106100e25761010080835404028352916020019161010d565b820191906000526020600020905b8154815290600101906020018083116100f057829003601f168201915b5050505050905090565b828054610123906102cc565b90600052602060002090601f016020900481019282610145576000855561018b565b82601f1061015e57805160ff191683800117855561018b565b8280016001018555821561018b579182015b8281111561018b578251825591602001919060010190610170565b5061019792915061019b565b5090565b5b80821115610197576000815560010161019c565b634e487b7160e01b600052604160045260246000fd5b6000602082840312156101d857600080fd5b813567ffffffffffffffff808211156101f057600080fd5b818401915084601f83011261020457600080fd5b813581811115610216576102166101b0565b604051601f8201601f19908116603f0116810190838211818310171561023e5761023e6101b0565b8160405282815287602084870101111561025757600080fd5b826020860160208301376000928101602001929092525095945050505050565b600060208083528351808285015260005b818110156102a457858101830151858201604001528201610288565b818111156102b6576000604083870101525b50601f01601f1916929092016040019392505050565b600181811c908216806102e057607f821691505b6020821081141561030157634e487b7160e01b600052602260045260246000fd5b5091905056fea264697066735822122070eb57da50846b381da60d3f4647c7040d72aad053c50d4ed6e571fbe2042aff64736f6c634300080b0033"};

    public static final String BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {"608060405234801561001057600080fd5b5060408051808201909152600d8082526c48656c6c6f2c20576f726c642160981b60209092019182526100459160009161004b565b5061011f565b828054610057906100e4565b90600052602060002090601f01602090048101928261007957600085556100bf565b82601f1061009257805160ff19168380011785556100bf565b828001600101855582156100bf579182015b828111156100bf5782518255916020019190600101906100a4565b506100cb9291506100cf565b5090565b5b808211156100cb57600081556001016100d0565b600181811c908216806100f857607f821691505b602082108114156101195763b95aa35560e01b600052602260045260246000fd5b50919050565b61033d8061012e6000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063299f7f9d1461003b5780633590b49f14610059575b600080fd5b61004361006e565b60405161005091906101b0565b60405180910390f35b61006c61006736600461021b565b610100565b005b60606000805461007d906102cc565b80601f01602080910402602001604051908101604052809291908181526020018280546100a9906102cc565b80156100f65780601f106100cb576101008083540402835291602001916100f6565b820191906000526020600020905b8154815290600101906020018083116100d957829003601f168201915b5050505050905090565b8051610113906000906020840190610117565b5050565b828054610123906102cc565b90600052602060002090601f016020900481019282610145576000855561018b565b82601f1061015e57805160ff191683800117855561018b565b8280016001018555821561018b579182015b8281111561018b578251825591602001919060010190610170565b5061019792915061019b565b5090565b5b80821115610197576000815560010161019c565b600060208083528351808285015260005b818110156101dd578581018301518582016040015282016101c1565b818111156101ef576000604083870101525b50601f01601f1916929092016040019392505050565b63b95aa35560e01b600052604160045260246000fd5b60006020828403121561022d57600080fd5b813567ffffffffffffffff8082111561024557600080fd5b818401915084601f83011261025957600080fd5b81358181111561026b5761026b610205565b604051601f8201601f19908116603f0116810190838211818310171561029357610293610205565b816040528281528760208487010111156102ac57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600181811c908216806102e057607f821691505b602082108114156103015763b95aa35560e01b600052602260045260246000fd5b5091905056fea2646970667358221220ad3331f4f52a10ab9c50f2e63a46fd49fab3847ff4e17912290db8f009f89c9464736f6c634300080b0033"};

    public static final String SM_BINARY = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[1833756220,698318749],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[0]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"selector\":[1322485854,898675871],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GET = "get";

    public static final String FUNC_SET = "set";

    protected HelloWorld(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public String get() throws ContractException {
        final Function function = new Function(FUNC_GET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Function getMethodGetRawFunction() throws ContractException {
        final Function function = new Function(FUNC_GET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return function;
    }

    public TransactionReceipt set(String n) {
        final Function function = new Function(
                FUNC_SET, 
                Arrays.<Type>asList(new Utf8String(n)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return executeTransaction(function);
    }

    public Function getMethodSetRawFunction(String n) throws ContractException {
        final Function function = new Function(FUNC_SET, 
                Arrays.<Type>asList(new Utf8String(n)),
                Arrays.<TypeReference<?>>asList());
        return function;
    }

    public String getSignedTransactionForSet(String n) {
        final Function function = new Function(
                FUNC_SET, 
                Arrays.<Type>asList(new Utf8String(n)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return createSignedTransaction(function);
    }

    public String set(String n, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_SET, 
                Arrays.<Type>asList(new Utf8String(n)),
                Collections.<TypeReference<?>>emptyList(), 0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getSetInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results = this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>(

                (String) results.get(0).getValue()
                );
    }

    public static HelloWorld load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new HelloWorld(contractAddress, client, credential);
    }

    public static HelloWorld deploy(Client client, CryptoKeyPair credential) throws
            ContractException {
        return deploy(HelloWorld.class, client, credential, getBinary(client.getCryptoSuite()), getABI(), null, null);
    }
}
