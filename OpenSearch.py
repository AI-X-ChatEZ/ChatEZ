from opensearchpy import OpenSearch
from typing import Any
from key import AWS


class Upload:
    env_type: str
    host = ""
    port: int
    auth = Any
    verbose: bool = True
    index_name: str = ""
    index_body = {'settings': {'index': {}}}
    client: Any

    def __init__(self, env_type: str, host: str, port: int, auth: Any,
                 chatbot_name: str, verbose: bool):
        self.env_type = env_type
        self.host = host
        self.port = port
        self.auth = auth
        self.index_name = chatbot_name
        self.verbose = verbose
        self.index_creation()

    def index_creation(self):
        if self.env_type == "local":
            self.client = OpenSearch(
                hosts=[{'host': self.host, 'port': self.port}],
                http_auth=self.auth,
                use_ssl=True,
                verify_certs=False
            )
        elif self.env_type == "web":
            self.client = OpenSearch(
                hosts=[{'host': self.host,
                        'port': self.port}],
                http_compress=True,
                http_auth=self.auth,
                timeout=300,
                use_ssl=True,
                verify_certs=True,
                ssl_assert_hostname=False,
                ssl_show_warn=False,
            )
        else:
            print("Check the env_type parameter.")

        response = self.client.indices.create(self.index_name,
                                              body=self.index_body)
        if self.verbose:
            print(response)

    def add_documents(self, metadata: list, tokens: list, vectors: list,
                          verbose: bool):
        if len(metadata) == len(tokens) == len(vectors):
            for i in range(len(metadata)):
                doc = {
                    'text': metadata[i],
                    'tokens': tokens[i],
                    'embedding_vectors': vectors[i]
                }

                response = self.client.index(
                    index=self.index_name,
                    id=self.index_name + '_' + str(i),
                    body=doc,
                    refresh=True
                )
                if verbose:
                    print(response)
        else:
            print("Check data length matched.")


if __name__ == '__main__':

    env_type = input("In which env will you use OpenSearch, 'local' or 'web'?")

    chatbot_name = "test"
    metadata = ['test_doc_1', 'test_doc_2', 'test_doc_3']
    tokens = [[11, 22, 33], [44, 55, 66], [77, 88, 99]]
    vectors = [[1.1e-02, 2.2e-01, 3.3e+00],
               [4.4e-02, 5.5e-01, 6.6e+00],
               [7.7e-02, 8.8e-01, 9.9e+00]]

    if env_type == 'local':
        local_uploader = Upload("local",
                                "localhost",
                                9200,
                                ('admin', 'admin'),
                                chatbot_name=chatbot_name,
                                verbose=True)

        local_uploader.add_documents(metadata, tokens, vectors, True)
    elif env_type == 'web':
        web_uploader = Upload("web",
                              AWS.host,
                              AWS.port,
                              AWS.auth,
                              chatbot_name=chatbot_name,
                              verbose=True)

        web_uploader.add_documents(metadata, tokens, vectors, True)
    else:
        print("Environment Type Error")