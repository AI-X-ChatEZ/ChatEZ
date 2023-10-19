from langchain.callbacks import get_openai_callback
import time
from typing import Any

from langchain import OpenAI
from langchain.chains import ConversationChain

from langchain.memory import ConversationBufferMemory
from langchain.memory import ConversationBufferWindowMemory
from langchain.memory import ConversationSummaryMemory
from langchain.memory import ConversationSummaryBufferMemory

from key import OpenAI as open_ai_api


class MemoryManagementWithOpenAI:
    llm: Any
    stack_style = ""
    memory: Any
    default = 2
    limit = 1000
    style = "buffer"

    def __init__(self, openai_key: str, model_name: str, stack_style: str = "buffer", k: int = 2):
        self.llm = OpenAI(temperature=0,
                          openai_api_key=openai_key,
                          model_name=model_name)
        self.style = stack_style
        self.default = k

    def memory_creation(self):
        if self.style == "buffer":
            self.memory = ConversationChain(llm=self.llm,
                                            memory=ConversationBufferMemory())
        elif self.style == "buffer window":
            self.memory = ConversationChain(llm=self.llm,
                                            memory=ConversationBufferWindowMemory(
                                                k=self.default)
                                            )
        elif self.style == "summary":
            self.memory = ConversationChain(llm=self.llm,
                                            memory=ConversationSummaryMemory(
                                                llm=self.llm)
                                            )
        elif self.style == "summary buffer window":
            self.memory = ConversationChain(llm=self.llm,
                                            memory=ConversationSummaryBufferMemory(
                                                llm=self.llm,
                                                max_token_limit=self.limit)
                                            )
        else:
            print("Check parameters.")

    def memory_checker(self, input_query):
        start = time.time()

        with get_openai_callback() as cb:
            try:
                result = self.memory.run(input_query)
                print('The number of total tokens: ', cb.total_tokens)
                print('The number of prompt tokens: ', cb.prompt_tokens)
                print('The number of completion tokens: ', cb.completion_tokens)
            except Exception:
                print('AN ERROR TAKES PLACE:', Exception)

        end = time.time()
        taken_time = f"It takes {(end - start)} seconds."

        return result, taken_time


if __name__ == '__main__':
    memory_management = MemoryManagementWithOpenAI(openai_key=open_ai_api.key,
                                                   model_name="text-davinci-003")

    memory_management.memory_creation()

    to_check = memory_management.memory('Hello!')
    print(to_check)

    checker_checker = memory_management.memory_checker('Hello!')
    print(checker_checker)