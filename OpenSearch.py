from opensearchpy import OpenSearch, RequestsHttpConnection, AWSV4SignerAuth
import boto3
from typing import Any


class Upload:
    url_type: str
    url = ""
    auth = Any
    verbose: bool = True
    index_name: str = ""
    index_body = {'settings': {'index': {}}}
    client: Any

    def __init__(self, url_type: str, url: str, auth: Any,
                 chatbot_name: str, verbose: bool):
        self.url_type = url_type
        self.url = url
        self.auth = auth
        self.index_name = chatbot_name
        # self.index_body = body
        self.verbose = verbose
        self.index_creation()

    def index_creation(self):
        if self.url_type == "local":
            self.client = OpenSearch(
                opensearch_url=self.url,
                http_auth=self.auth,
                use_ssl=True,
                verify_certs=False
            )
        elif self.url_type == "web":
            self.client = OpenSearch(
                opensearch_url=self.url,
                http_auth=self.auth,
                timeout=300,
                use_ssl=True,
                verify_certs=True,
                connection_class=RequestsHttpConnection,
            )
        else:
            print("Check the url_type parameter.")

        response = self.client.indices.create(self.index_name,
                                              body=self.index_body)
        if self.verbose:
            print(response)

    def add_documents(self, id: list, metadata: list, tokens: list, vectors: list,
                      verbose: bool):
        if len(id) == len(metadata) == len(tokens) == len(vectors):
            for i in range(len(metadata)):
                doc = {
                    'text': metadata[i],
                    'tokens': tokens[i],
                    'embedding_vectors': vectors[i]
                }

                response = self.client.index(
                    index=self.index_name,
                    body=doc,
                    id=id[i],
                    refresh=True
                )
                if verbose:
                    print(response)
        else:
            print("Check data length matched.")


if __name__ == '__main__':

    index_id = list(range(3))
    metadata = ['test_1', 'test_2', 'test_3']
    tokens = [11, 22, 33]
    vectors = [1.1e-02, 2.2e-01, 3.3e+00]

    local_uploader = Upload("local",
                            "https://localhost:9200",
                            ('admin', 'admin'),
                            "test",
                            verbose=True)

    local_uploader.add_documents(index_id, metadata,
                                 tokens, vectors, True)